package com.wangindustries.badmintondbBackend.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStringingRequest {

    private UUID stringerUserId;

    private UUID ownerUserId;

    private String ownerName;

    @NotBlank(message = "Racket make is required")
    private String racketMake;

    @NotBlank(message = "Racket model is required")
    private String racketModel;

    private String stringType;

    @NotNull(message = "Mains tension is required")
    @Positive(message = "Mains tension must be a positive number")
    private Double mainsTensionLbs;

    @NotNull(message = "Crosses tension is required")
    @Positive(message = "Crosses tension must be a positive number")
    private Double crossesTensionLbs;
}
