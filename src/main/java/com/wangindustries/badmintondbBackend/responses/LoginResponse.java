package com.wangindustries.badmintondbBackend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UUID userId;
    private String username;
    private String givenName;
    private String familyName;
}
