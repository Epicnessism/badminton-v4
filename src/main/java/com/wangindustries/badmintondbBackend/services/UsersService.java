package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.repositories.UsersRepository;
import com.wangindustries.badmintondbBackend.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PasswordEncryptionService passwordEncryptionService;

    public User findByUserId(UUID userId) {
        return usersRepository.getUser(userId);
    }

    public User createUser(CreateUserRequest request) {
        logger.info("Creating user with request: {}", request);

        User existingUser = usersRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            logger.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }

        UUID userId = UUID.randomUUID();
        String encryptedPassword = passwordEncryptionService.encryptPassword(request.getPassword());

        User user = new User();
        user.setPK(User.createPk(userId));
        user.setSK(User.createSk());
        user.setGsiPk(User.createGsiPk(request.getGivenName()));
        user.setGsiSk(User.createGsiSk(request.getFamilyName()));
        user.setUsernameGsiPk(User.createUsernameGsiPk(request.getUsername()));
        user.setUserId(userId);
        user.setGivenName(request.getGivenName());
        user.setFamilyName(request.getFamilyName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setAge(request.getAge());
        user.setBirthday(request.getBirthday());
        user.setEncryptedPassword(encryptedPassword);
        user.setCreatedAt(Instant.now());

        usersRepository.saveUser(user);
        logger.info("Successfully created user: {}", user);

        return user;
    }

    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }
}
