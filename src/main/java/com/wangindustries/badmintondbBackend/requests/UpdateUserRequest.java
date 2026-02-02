package com.wangindustries.badmintondbBackend.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String givenName;

    private String familyName;

    @Email(message = "Email must be a valid email address")
    private String email;

    private LocalDate birthday;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
