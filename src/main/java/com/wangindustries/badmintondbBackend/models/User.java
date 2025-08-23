package com.wangindustries.badmintondbBackend.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@DynamoDbBean
public class User {
    public static final String pkSyntax = "USER#%s";
    private String pk;
    private String sk;

    @DynamoDbPartitionKey
    public String getPk() {
        return this.pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return this.sk;
    }

    private UUID userId;
    private String givenName;
    private String familyName;


    public static String createPk(final UUID userId) {
        return pkSyntax.formatted(userId.toString());
    }

//    private Gender gender;
//    private String email;
//    private boolean isStringer;
//    private String username;
//    private String password;
//    private Date dateOfBirth;
//    private List<Racket> rackets;
}