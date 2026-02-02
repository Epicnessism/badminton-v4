package com.wangindustries.badmintondbBackend.models;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.UUID;

@DynamoDbBean
public class Stringing {
    public static final String PK_SYNTAX = "STRINGING#%s";
    public static final String SK_DETAILS = "DETAILS";
    public static final String SK_OWNER_SYNTAX = "OWNER#%s";
    public static final String NAME_GSI = "name-index";
    public static final String GSI_STRINGER_PK_SYNTAX = "STRINGER#%s";
    public static final String GSI_OWNER_PK_SYNTAX = "OWNER#%s";

    private String PK;
    private String SK;
    private String gsiPk;
    private String gsiSk;

    private UUID stringingId;
    private UUID stringerUserId;
    private UUID ownerUserId;
    private String ownerName;
    private String racketMake;
    private String racketModel;
    private String stringType;
    private String stringColor;
    private Double mainsTensionLbs;
    private Double crossesTensionLbs;
    private StringingState state;
    private Instant createdAt;
    private Instant requestedAt;
    private Instant receivedAt;
    private Instant inProgressAt;
    private Instant finishedAt;
    private Instant completedAt;
    private Instant failedAt;
    private Instant failedCompletedAt;
    private Instant declinedAt;

    @DynamoDbPartitionKey
    public String getPK() {
        return this.PK;
    }

    public void setPK(String PK) {
        this.PK = PK;
    }

    @DynamoDbSortKey
    public String getSK() {
        return this.SK;
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

    public UUID getStringingId() {
        return stringingId;
    }

    public void setStringingId(UUID stringingId) {
        this.stringingId = stringingId;
    }

    public UUID getStringerUserId() {
        return stringerUserId;
    }

    public void setStringerUserId(UUID stringerUserId) {
        this.stringerUserId = stringerUserId;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRacketMake() {
        return racketMake;
    }

    public void setRacketMake(String racketMake) {
        this.racketMake = racketMake;
    }

    public String getRacketModel() {
        return racketModel;
    }

    public void setRacketModel(String racketModel) {
        this.racketModel = racketModel;
    }

    public String getStringType() {
        return stringType;
    }

    public void setStringType(String stringType) {
        this.stringType = stringType;
    }

    public String getStringColor() {
        return stringColor;
    }

    public void setStringColor(String stringColor) {
        this.stringColor = stringColor;
    }

    public Double getMainsTensionLbs() {
        return mainsTensionLbs;
    }

    public void setMainsTensionLbs(Double mainsTensionLbs) {
        this.mainsTensionLbs = mainsTensionLbs;
    }

    public Double getCrossesTensionLbs() {
        return crossesTensionLbs;
    }

    public void setCrossesTensionLbs(Double crossesTensionLbs) {
        this.crossesTensionLbs = crossesTensionLbs;
    }

    public StringingState getState() {
        return state;
    }

    public void setState(StringingState state) {
        this.state = state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getInProgressAt() {
        return inProgressAt;
    }

    public void setInProgressAt(Instant inProgressAt) {
        this.inProgressAt = inProgressAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public Instant getFailedCompletedAt() {
        return failedCompletedAt;
    }

    public void setFailedCompletedAt(Instant failedCompletedAt) {
        this.failedCompletedAt = failedCompletedAt;
    }

    public Instant getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(Instant declinedAt) {
        this.declinedAt = declinedAt;
    }

    public static String createPk(final UUID stringingId) {
        return PK_SYNTAX.formatted(stringingId.toString());
    }

    public static String createSkDetails() {
        return SK_DETAILS;
    }

    public static String createSkOwner(final UUID ownerUserId) {
        return SK_OWNER_SYNTAX.formatted(ownerUserId.toString());
    }

    public static String createGsiStringerPk(final UUID stringerUserId) {
        return GSI_STRINGER_PK_SYNTAX.formatted(stringerUserId.toString());
    }

    public static String createGsiOwnerPk(final UUID ownerUserId) {
        return GSI_OWNER_PK_SYNTAX.formatted(ownerUserId.toString());
    }

    public static String createGsiSk(final UUID stringingId) {
        return PK_SYNTAX.formatted(stringingId.toString());
    }

    @Override
    public String toString() {
        return "Stringing{" +
                "PK='" + PK + '\'' +
                ", SK='" + SK + '\'' +
                ", stringingId=" + stringingId +
                ", stringerUserId=" + stringerUserId +
                ", ownerUserId=" + ownerUserId +
                ", ownerName='" + ownerName + '\'' +
                ", racketMake='" + racketMake + '\'' +
                ", racketModel='" + racketModel + '\'' +
                ", stringType='" + stringType + '\'' +
                ", mainsTensionLbs=" + mainsTensionLbs +
                ", crossesTensionLbs=" + crossesTensionLbs +
                ", state=" + state +
                ", createdAt=" + createdAt +
                '}';
    }
}
