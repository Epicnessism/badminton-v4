package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.repositories.UsersRepository;
import com.wangindustries.badmintondbBackend.requests.LoginRequest;
import com.wangindustries.badmintondbBackend.responses.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Autowired
    private JwtService jwtService;

    public LoginResponse authenticate(LoginRequest request) {
        logger.info("Attempting authentication for username: {}", request.getUsername());

        User user = usersRepository.findByUsername(request.getUsername());

        if (user == null) {
            logger.warn("User not found: {}", request.getUsername());
            return null;
        }

        if (!passwordEncryptionService.matches(request.getPassword(), user.getEncryptedPassword())) {
            logger.warn("Invalid password for user: {}", request.getUsername());
            return null;
        }

        String token = jwtService.generateToken(user.getUserId(), user.getUsername());

        logger.info("Authentication successful for user: {}", request.getUsername());

        return new LoginResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getGivenName(),
                user.getFamilyName()
        );
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}
