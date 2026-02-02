package com.wangindustries.badmintondbBackend.repositories;

import com.wangindustries.badmintondbBackend.models.UserAnalytics;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

@Repository
public class AnalyticsRepository {
    private final DynamoDbTable<UserAnalytics> analyticsTable;

    public AnalyticsRepository(DynamoDbEnhancedClient enhancedClient) {
        analyticsTable = enhancedClient.table("badmintonDb", TableSchema.fromClass(UserAnalytics.class));
    }

    public UserAnalytics getAnalytics(UUID userId) {
        return analyticsTable.getItem(Key.builder()
                .partitionValue(UserAnalytics.createPk(userId))
                .sortValue(UserAnalytics.createSk())
                .build());
    }

    public void saveAnalytics(UserAnalytics analytics) {
        analyticsTable.putItem(analytics);
    }
}
