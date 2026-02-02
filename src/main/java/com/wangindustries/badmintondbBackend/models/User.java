package com.wangindustries.badmintondbBackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import java.time.Instant;
import java.time.LocalDate;

import java.util.UUID;

@DynamoDbBean
public class User {
    public static final String PK_SYNTAX = "USER#%s";
    public static final String SK_SYNTAX = "PROFILE";
    public static final String NAME_GSI = "name-index";
    public static final String USERNAME_GSI = "username-index";
    public static final String GSI_PK_SYNTAX = "NAME#%s";
    private String PK;
    private String SK;
    private String gsiPk;
    private String gsiSk;
    private String usernameGsiPk;

    @DynamoDbPartitionKey
    public String getPK() {
        return this.PK;
    }

    @DynamoDbSortKey
    public String getSK() {
        return this.SK;
    }

    private UUID userId;
    private String givenName;
    private String familyName;
    private String email;
    private String username;
    private LocalDate birthday;
    private String encryptedPassword;
    private Instant createdAt;
    private Boolean isStringer;


    public static String createPk(final UUID userId) {
        return PK_SYNTAX.formatted(userId.toString());
    }

    public void setPK(String PK) {
        this.PK = PK;
    }

    public void setSK(String SK) {
        this.SK = SK;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {NAME_GSI})
    public String getGsiPk() {
        return this.gsiPk;
    }

    public void setGsiPk(String gsiPk) {
        this.gsiPk = gsiPk;
    }

    @DynamoDbSecondarySortKey(indexNames = {NAME_GSI})
    public String getGsiSk() {
        return this.gsiSk;
    }

    public void setGsiSk(String gsiSk) {
        this.gsiSk = gsiSk;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {USERNAME_GSI})
    public String getUsernameGsiPk() {
        return usernameGsiPk;
    }

    public void setUsernameGsiPk(String usernameGsiPk) {
        this.usernameGsiPk = usernameGsiPk;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @JsonIgnore
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsStringer() {
        return isStringer;
    }

    public void setIsStringer(Boolean isStringer) {
        this.isStringer = isStringer;
    }

    public static String createSk() {
        return SK_SYNTAX;
    }

    public static String createGsiPk(final String givenName) {
        return GSI_PK_SYNTAX.formatted(givenName.toUpperCase());
    }

    public static String createGsiSk(final String familyName) {
        return familyName.toUpperCase();
    }

    public static String createUsernameGsiPk(final String username) {
        return "USERNAME#" + username.toLowerCase();
    }

    @Override
    public String toString() {
        return "User{" +
                "pk='" + PK + '\'' +
                ", sk='" + SK + '\'' +
                ", userId=" + userId +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", encryptedPassword='[PROTECTED]'" +
                '}';
    }
}