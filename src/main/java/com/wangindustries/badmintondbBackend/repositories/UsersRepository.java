package com.wangindustries.badmintondbBackend.repositories;

import com.wangindustries.badmintondbBackend.models.User;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

/**
 * This Repository method handles communicating with the DynamoDB Table via the DDB Enhanced Client
 * TODO TBD: Conversions of types should be handled by the UsersService ORRR this repository handles type conversions
 */
@Service
public class UsersRepository {
    DynamoDbTable<User> userTable;

    public UsersRepository(DynamoDbEnhancedClient enhancedClient) {
        userTable = enhancedClient.table("bst_db", TableSchema.fromClass(User.class));
    }

    public User getUser(final UUID userId) {
        userTable.getItem(Key.builder().partitionValue(User.createPk(userId)).build());
    }
}
