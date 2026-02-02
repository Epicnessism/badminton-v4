package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.models.MonthlyCount;
import com.wangindustries.badmintondbBackend.models.Stringing;
import com.wangindustries.badmintondbBackend.models.StringingState;
import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.models.UserAnalytics;
import com.wangindustries.badmintondbBackend.repositories.AnalyticsRepository;
import com.wangindustries.badmintondbBackend.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private StringingService stringingService;

    @Autowired
    private UsersRepository usersRepository;

    public UserAnalytics getAnalytics(UUID userId, boolean forceRefresh) {
        log.info("Getting analytics for user {} (forceRefresh={})", userId, forceRefresh);

        if (!forceRefresh) {
            UserAnalytics cached = analyticsRepository.getAnalytics(userId);
            if (cached != null && isCacheValid(cached.getComputedAt())) {
                log.info("Returning cached analytics for user {} (computed at {})", userId, cached.getComputedAt());
                return cached;
            }
        }

        return computeAndSaveAnalytics(userId);
    }

    private boolean isCacheValid(Instant computedAt) {
        if (computedAt == null) return false;
        return Duration.between(computedAt, Instant.now()).compareTo(CACHE_TTL) < 0;
    }

    private UserAnalytics computeAndSaveAnalytics(UUID userId) {
        log.info("Computing fresh analytics for user {}", userId);

        User user = usersRepository.getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        UserAnalytics analytics = new UserAnalytics();
        analytics.setPK(UserAnalytics.createPk(userId));
        analytics.setSK(UserAnalytics.createSk());
        analytics.setUserId(userId);
        analytics.setComputedAt(Instant.now());

        // Compute owner stats - includes all stringings where user is the owner
        List<Stringing> ownerStringings = stringingService.getStringingsByOwnerUserId(userId);
        computeOwnerStats(analytics, ownerStringings);

        // Compute stringer stats if user is a stringer
        // This includes stringings where user is the stringer (even if they're also the owner)
        if (Boolean.TRUE.equals(user.getIsStringer())) {
            List<Stringing> stringerStringings = stringingService.getStringingsByStringerUserId(userId);
            // Always set stringer stats for stringer users, even if empty
            // This ensures the UI shows the stringer section with 0 values rather than hiding it
            computeStringerStats(analytics, stringerStringings != null ? stringerStringings : new ArrayList<>());
        }

        analyticsRepository.saveAnalytics(analytics);
        log.info("Saved fresh analytics for user {}", userId);

        return analytics;
    }

    private void computeOwnerStats(UserAnalytics analytics, List<Stringing> stringings) {
        analytics.setTotalStringingsAsOwner(stringings.size());

        // Stringings by state
        Map<String, Integer> byState = stringings.stream()
                .filter(s -> s.getState() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getState().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        analytics.setStringingsByState(byState);

        // String type usage
        Map<String, Integer> stringTypes = stringings.stream()
                .filter(s -> s.getStringType() != null && !s.getStringType().isEmpty())
                .collect(Collectors.groupingBy(
                        Stringing::getStringType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        analytics.setStringTypeUsage(stringTypes);

        // Racket usage (make + model)
        Map<String, Integer> rackets = stringings.stream()
                .filter(s -> s.getRacketMake() != null && s.getRacketModel() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getRacketMake() + " " + s.getRacketModel(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        analytics.setRacketUsage(rackets);

        // Most used tension combination (mains x crosses)
        Map<String, Long> tensionCombinations = stringings.stream()
                .filter(s -> s.getMainsTensionLbs() != null && s.getCrossesTensionLbs() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getMainsTensionLbs().intValue() + " x " + s.getCrossesTensionLbs().intValue() + " lbs",
                        Collectors.counting()
                ));
        
        tensionCombinations.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> {
                    analytics.setMostUsedTensionCombination(entry.getKey());
                    analytics.setMostUsedTensionCount(entry.getValue().intValue());
                });

        // Monthly trend (last 12 months)
        Map<String, Integer> monthlyMap = stringings.stream()
                .filter(s -> s.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        s -> MONTH_FORMATTER.format(s.getCreatedAt().atZone(ZoneId.systemDefault())),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));

        List<MonthlyCount> trend = monthlyMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlyCount(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        analytics.setMonthlyTrend(trend);

        // Top stringers - need to look up stringer names
        Map<UUID, Long> stringerCounts = stringings.stream()
                .filter(s -> s.getStringerUserId() != null)
                .collect(Collectors.groupingBy(Stringing::getStringerUserId, Collectors.counting()));

        Map<String, Integer> topStringers = new LinkedHashMap<>();
        stringerCounts.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    User stringer = usersRepository.getUser(entry.getKey());
                    String name = stringer != null 
                            ? stringer.getGivenName() + " " + stringer.getFamilyName()
                            : "Unknown";
                    topStringers.put(name, entry.getValue().intValue());
                });
        analytics.setTopStringers(topStringers);
    }

    private void computeStringerStats(UserAnalytics analytics, List<Stringing> stringings) {
        analytics.setTotalStringingsAsStringer(stringings.size());

        // Top customers - need to look up owner names
        Map<UUID, Long> ownerCounts = stringings.stream()
                .filter(s -> s.getOwnerUserId() != null)
                .collect(Collectors.groupingBy(Stringing::getOwnerUserId, Collectors.counting()));

        Map<String, Integer> topCustomers = new LinkedHashMap<>();
        ownerCounts.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    User owner = usersRepository.getUser(entry.getKey());
                    String name = owner != null 
                            ? owner.getGivenName() + " " + owner.getFamilyName()
                            : "Unknown";
                    topCustomers.put(name, entry.getValue().intValue());
                });
        analytics.setTopCustomers(topCustomers);

        // Average completion time (received -> finished or completed)
        List<Long> completionTimes = stringings.stream()
                .filter(s -> s.getReceivedAt() != null && 
                        (s.getFinishedAt() != null || s.getCompletedAt() != null))
                .map(s -> {
                    Instant end = s.getFinishedAt() != null ? s.getFinishedAt() : s.getCompletedAt();
                    return Duration.between(s.getReceivedAt(), end).toHours();
                })
                .collect(Collectors.toList());

        double avgCompletionTime = completionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        analytics.setAverageCompletionTimeHours(Math.round(avgCompletionTime * 10) / 10.0);

        // Success rate - only count stringings in final states that represent actual stringing work
        // COMPLETED and FINISHED_BUT_NOT_PICKED_UP = success
        // FAILED_COMPLETED and FAILED_BUT_NOT_PICKED_UP = failure
        // CANCELED and DECLINED are final but don't represent work done, so excluded
        long successful = stringings.stream()
                .filter(s -> s.getState() == StringingState.COMPLETED || 
                        s.getState() == StringingState.FINISHED_BUT_NOT_PICKED_UP)
                .count();
        long failed = stringings.stream()
                .filter(s -> s.getState() == StringingState.FAILED_COMPLETED || 
                        s.getState() == StringingState.FAILED_BUT_NOT_PICKED_UP)
                .count();
        long totalCompleted = successful + failed;
        double successRate = totalCompleted > 0 ? (double) successful / totalCompleted * 100 : 100.0;
        analytics.setSuccessRate(Math.round(successRate * 10) / 10.0);

        // Busiest month
        Map<String, Long> monthlyCountsStringer = stringings.stream()
                .filter(s -> s.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        s -> MONTH_FORMATTER.format(s.getCreatedAt().atZone(ZoneId.systemDefault())),
                        Collectors.counting()
                ));
        
        String busiestMonth = monthlyCountsStringer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        analytics.setBusiestMonth(busiestMonth);

        // String type usage for stringer
        Map<String, Integer> stringerStringTypes = stringings.stream()
                .filter(s -> s.getStringType() != null && !s.getStringType().isEmpty())
                .collect(Collectors.groupingBy(
                        Stringing::getStringType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        analytics.setStringerStringTypeUsage(stringerStringTypes);

        // Racket usage for stringer (make + model)
        Map<String, Integer> stringerRackets = stringings.stream()
                .filter(s -> s.getRacketMake() != null && s.getRacketModel() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getRacketMake() + " " + s.getRacketModel(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        analytics.setStringerRacketUsage(stringerRackets);

        // Monthly trend for stringer
        List<MonthlyCount> stringerTrend = monthlyCountsStringer.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new MonthlyCount(e.getKey(), e.getValue().intValue()))
                .collect(Collectors.toList());
        analytics.setStringerMonthlyTrend(stringerTrend);
    }
}
