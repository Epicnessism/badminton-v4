package com.wangindustries.badmintondbBackend.services;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidationService {

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String PASSWORD_TOO_SHORT_MESSAGE = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";

    public void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(PASSWORD_TOO_SHORT_MESSAGE);
        }
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
}
