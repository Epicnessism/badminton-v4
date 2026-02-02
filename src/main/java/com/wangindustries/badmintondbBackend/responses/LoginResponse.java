package com.wangindustries.badmintondbBackend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UUID userId;
    private String username;
    private String givenName;
    private String familyName;
    private String email;
    private LocalDate birthday;
    private Instant createdAt;
}
