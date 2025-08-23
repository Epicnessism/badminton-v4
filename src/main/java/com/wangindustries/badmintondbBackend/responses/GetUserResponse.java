package com.wangindustries.badmintondbBackend.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUserResponse {
    private final UUID userId;
}
