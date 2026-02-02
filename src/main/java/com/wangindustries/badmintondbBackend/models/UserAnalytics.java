package com.wangindustries.badmintondbBackend.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@DynamoDbBean
public class UserAnalytics {
    public static final String PK_SYNTAX = "ANALYTICS#%s";
    public static final String SK_SYNTAX = "SUMMARY";

    private String PK;
    private String SK;
    private UUID userId;
    private Instant computedAt;

    // Owner stats (for all users)
    private Integer totalStringingsAsOwner;
    private Map<String, Integer> stringingsByState;
    private Map<String, Integer> stringTypeUsage;
    private Map<String, Integer> racketUsage;
    private String mostUsedTensionCombination;
    private Integer mostUsedTensionCount;
    private List<MonthlyCount> monthlyTrend;
    private Map<String, Integer> topStringers;

    // Stringer stats (only for stringers)
    private Integer totalStringingsAsStringer;
    private Map<String, Integer> topCustomers;
    private Double averageCompletionTimeHours;
    private Double successRate;
    private String busiestMonth;
    private Map<String, Integer> stringerStringTypeUsage;
    private Map<String, Integer> stringerRacketUsage;
    private List<MonthlyCount> stringerMonthlyTrend;

    @DynamoDbPartitionKey
    public String getPK() {
        return PK;
    }

    public void setPK(String PK) {
        this.PK = PK;
    }

    @DynamoDbSortKey
    public String getSK() {
        return SK;
    }

    public void setSK(String SK) {
        this.SK = SK;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(Instant computedAt) {
        this.computedAt = computedAt;
    }

    public Integer getTotalStringingsAsOwner() {
        return totalStringingsAsOwner;
    }

    public void setTotalStringingsAsOwner(Integer totalStringingsAsOwner) {
        this.totalStringingsAsOwner = totalStringingsAsOwner;
    }

    public Map<String, Integer> getStringingsByState() {
        return stringingsByState;
    }

    public void setStringingsByState(Map<String, Integer> stringingsByState) {
        this.stringingsByState = stringingsByState;
    }

    public Map<String, Integer> getStringTypeUsage() {
        return stringTypeUsage;
    }

    public void setStringTypeUsage(Map<String, Integer> stringTypeUsage) {
        this.stringTypeUsage = stringTypeUsage;
    }

    public Map<String, Integer> getRacketUsage() {
        return racketUsage;
    }

    public void setRacketUsage(Map<String, Integer> racketUsage) {
        this.racketUsage = racketUsage;
    }

    public String getMostUsedTensionCombination() {
        return mostUsedTensionCombination;
    }

    public void setMostUsedTensionCombination(String mostUsedTensionCombination) {
        this.mostUsedTensionCombination = mostUsedTensionCombination;
    }

    public Integer getMostUsedTensionCount() {
        return mostUsedTensionCount;
    }

    public void setMostUsedTensionCount(Integer mostUsedTensionCount) {
        this.mostUsedTensionCount = mostUsedTensionCount;
    }

    public List<MonthlyCount> getMonthlyTrend() {
        return monthlyTrend;
    }

    public void setMonthlyTrend(List<MonthlyCount> monthlyTrend) {
        this.monthlyTrend = monthlyTrend;
    }

    public Map<String, Integer> getTopStringers() {
        return topStringers;
    }

    public void setTopStringers(Map<String, Integer> topStringers) {
        this.topStringers = topStringers;
    }

    public Integer getTotalStringingsAsStringer() {
        return totalStringingsAsStringer;
    }

    public void setTotalStringingsAsStringer(Integer totalStringingsAsStringer) {
        this.totalStringingsAsStringer = totalStringingsAsStringer;
    }

    public Map<String, Integer> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(Map<String, Integer> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public Double getAverageCompletionTimeHours() {
        return averageCompletionTimeHours;
    }

    public void setAverageCompletionTimeHours(Double averageCompletionTimeHours) {
        this.averageCompletionTimeHours = averageCompletionTimeHours;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }

    public String getBusiestMonth() {
        return busiestMonth;
    }

    public void setBusiestMonth(String busiestMonth) {
        this.busiestMonth = busiestMonth;
    }

    public Map<String, Integer> getStringerStringTypeUsage() {
        return stringerStringTypeUsage;
    }

    public void setStringerStringTypeUsage(Map<String, Integer> stringerStringTypeUsage) {
        this.stringerStringTypeUsage = stringerStringTypeUsage;
    }

    public Map<String, Integer> getStringerRacketUsage() {
        return stringerRacketUsage;
    }

    public void setStringerRacketUsage(Map<String, Integer> stringerRacketUsage) {
        this.stringerRacketUsage = stringerRacketUsage;
    }

    public List<MonthlyCount> getStringerMonthlyTrend() {
        return stringerMonthlyTrend;
    }

    public void setStringerMonthlyTrend(List<MonthlyCount> stringerMonthlyTrend) {
        this.stringerMonthlyTrend = stringerMonthlyTrend;
    }

    public static String createPk(UUID userId) {
        return PK_SYNTAX.formatted(userId.toString());
    }

    public static String createSk() {
        return SK_SYNTAX;
    }
}
