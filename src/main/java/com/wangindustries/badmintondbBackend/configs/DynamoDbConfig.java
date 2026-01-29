package com.wangindustries.badmintondbBackend.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    /** use default create method to create dynamo client bean.
     * Not sure why we need to specify this ourselves and is not default
     * @return DynamoDbEnhancedClient
     */
    @Bean
    @ConditionalOnProperty(name = "dynamodb.local.enabled", havingValue = "false")
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }

    @Bean
    @ConditionalOnProperty(name = "dynamodb.local.enabled", havingValue = "true")
    public DynamoDbEnhancedClient dynamoDbClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create("fakeKey", "fakeSecret");
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(
                        DynamoDbClient.builder()
                                .endpointOverride(URI.create("http://localhost:8000"))
                                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                                .region(Region.US_EAST_1)
                                .build())
                .build();
    }
}
