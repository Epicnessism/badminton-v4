package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.repositories.UsersRepository;
import com.wangindustries.badmintondbBackend.requests.LoginRequest;
import com.wangindustries.badmintondbBackend.responses.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    @Autowired
    private JwtService jwtService;

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Attempting authentication for username: {}", request.getUsername());

        User user = usersRepository.findByUsername(request.getUsername());

        if (user == null) {
            log.warn("User not found: {}", request.getUsername());
            return null;
        }

        if (!passwordEncryptionService.matches(request.getPassword(), user.getEncryptedPassword())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            return null;
        }

        String token = jwtService.generateToken(user.getUserId(), user.getUsername());

        log.info("Authentication successful for user: {}", request.getUsername());

        return new LoginResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getEmail(),
                user.getBirthday(),
                user.getCreatedAt()
        );
    }

    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}
