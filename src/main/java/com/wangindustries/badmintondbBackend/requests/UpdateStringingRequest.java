package com.wangindustries.badmintondbBackend.requests;

import com.wangindustries.badmintondbBackend.models.StringingState;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for updating a Stringing entity.
 * All fields are optional - only non-null fields will be updated.
 */
@Data
public class UpdateStringingRequest {

    private UUID stringerUserId;

    private UUID ownerUserId;

    private String ownerName;

    private String racketMake;

    private String racketModel;

    private String stringType;

    @Positive(message = "Mains tension must be a positive number")
    private Double mainsTensionLbs;

    @Positive(message = "Crosses tension must be a positive number")
    private Double crossesTensionLbs;

    private StringingState state;
}
