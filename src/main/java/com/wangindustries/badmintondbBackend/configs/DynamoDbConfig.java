package com.wangindustries.badmintondbBackend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    /** use default create method to create dynamo client bean.
     * Not sure why we need to specify this ourselves and is not default
     * @return DynamoDbEnhancedClient
     */
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }
}
