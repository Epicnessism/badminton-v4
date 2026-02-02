package com.wangindustries.badmintondbBackend.controllers;

import com.wangindustries.badmintondbBackend.models.UserAnalytics;
import com.wangindustries.badmintondbBackend.services.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAnalytics> getAnalytics(
            @PathVariable UUID userId,
            @RequestParam(value = "refresh", defaultValue = "false") boolean refresh
    ) {
        log.info("Received analytics request for user {} (refresh={})", userId, refresh);
        try {
            UserAnalytics analytics = analyticsService.getAnalytics(userId, refresh);
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("User not found: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to get analytics for user {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/{userId}/refresh")
    public ResponseEntity<UserAnalytics> refreshAnalytics(@PathVariable UUID userId) {
        log.info("Received force refresh analytics request for user {}", userId);
        try {
            UserAnalytics analytics = analyticsService.getAnalytics(userId, true);
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("User not found: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Failed to refresh analytics for user {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
