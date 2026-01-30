package com.wangindustries.badmintondbBackend.repositories;

import com.wangindustries.badmintondbBackend.models.User;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.UUID;

/**
 * This Repository method handles communicating with the DynamoDB Table via the DDB Enhanced Client
 * TODO TBD: Conversions of types should be handled by the UsersService ORRR this repository handles type conversions
 */
@Service
public class UsersRepository {
    private final DynamoDbTable<User> userTable;
    private final DynamoDbIndex<User> usernameIndex;

    public UsersRepository(DynamoDbEnhancedClient enhancedClient) {
        userTable = enhancedClient.table("badmintonDb", TableSchema.fromClass(User.class));
        usernameIndex = userTable.index(User.USERNAME_GSI);
    }

    public User getUser(final UUID userId) {
        return userTable.getItem(Key.builder().partitionValue(User.createPk(userId)).build());
    }

    public User findByUsername(final String username) {
        String usernameGsiPk = User.createUsernameGsiPk(username);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(usernameGsiPk).build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(1)
                .build();

        return usernameIndex.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElse(null);
    }

    public void saveUser(final User user) {
        userTable.putItem(user);
    }
}
