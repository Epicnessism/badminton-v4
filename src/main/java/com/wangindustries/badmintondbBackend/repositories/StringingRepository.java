package com.wangindustries.badmintondbBackend.repositories;

import com.wangindustries.badmintondbBackend.models.Stringing;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StringingRepository {

    private final DynamoDbTable<Stringing> stringingTable;
    private final DynamoDbIndex<Stringing> nameIndex;

    public StringingRepository(DynamoDbEnhancedClient enhancedClient) {
        this.stringingTable = enhancedClient.table("badmintonDb", TableSchema.fromClass(Stringing.class));
        this.nameIndex = stringingTable.index(Stringing.NAME_GSI);
    }

    public void saveStringing(final Stringing stringing) {
        stringingTable.putItem(stringing);
    }

    public Stringing getStringing(final UUID stringingId) {
        return stringingTable.getItem(Key.builder()
                .partitionValue(Stringing.createPk(stringingId))
                .sortValue(Stringing.createSkDetails())
                .build());
    }

    public List<Stringing> getStringingsByStringerUserId(final UUID stringerUserId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(Stringing.createGsiStringerPk(stringerUserId))
                .build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return nameIndex.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public List<Stringing> getStringingsByOwnerUserId(final UUID ownerUserId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(Stringing.createGsiOwnerPk(ownerUserId))
                .build());

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return nameIndex.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }

    public void updateStringing(final Stringing stringing) {
        stringingTable.updateItem(stringing);
    }

    public void deleteOwnerIndexItem(final UUID stringingId, final UUID ownerUserId) {
        stringingTable.deleteItem(Key.builder()
                .partitionValue(Stringing.createPk(stringingId))
                .sortValue(Stringing.createSkOwner(ownerUserId))
                .build());
    }
}
