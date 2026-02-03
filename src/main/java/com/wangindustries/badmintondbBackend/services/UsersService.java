package com.wangindustries.badmintondbBackend.services;

import com.wangindustries.badmintondbBackend.models.User;
import com.wangindustries.badmintondbBackend.repositories.UsersRepository;
import com.wangindustries.badmintondbBackend.requests.CreateUserRequest;
import com.wangindustries.badmintondbBackend.requests.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class UsersService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PasswordEncryptionService passwordEncryptionService;

    @Autowired
    PasswordValidationService passwordValidationService;

    public User createUser(CreateUserRequest request) {
        log.info("Creating user with request: {}", request);

        User existingUser = usersRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }

        passwordValidationService.validatePassword(request.getPassword());

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
        user.setBirthday(request.getBirthday());
        user.setEncryptedPassword(encryptedPassword);
        user.setCreatedAt(Instant.now());
        user.setIsStringer(request.getIsStringer() != null ? request.getIsStringer() : false);

        usersRepository.saveUser(user);
        log.info("Successfully created user: {}", user);

        return user;
    }

    public User getUser(UUID userId) {
        return usersRepository.getUser(userId);
    }

    public User findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public List<User> listUsers() {
        return usersRepository.listUsers();
    }

    public List<User> listStringers() {
        return usersRepository.listStringers();
    }

    public User updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Updating user {} with request: {}", userId, request);

        User existingUser = usersRepository.getUser(userId);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        if (request.getGivenName() != null) {
            existingUser.setGivenName(request.getGivenName());
            existingUser.setGsiPk(User.createGsiPk(request.getGivenName()));
        }
        if (request.getFamilyName() != null) {
            existingUser.setFamilyName(request.getFamilyName());
            existingUser.setGsiSk(User.createGsiSk(request.getFamilyName()));
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getBirthday() != null) {
            existingUser.setBirthday(request.getBirthday());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            passwordValidationService.validatePassword(request.getPassword());
            String encryptedPassword = passwordEncryptionService.encryptPassword(request.getPassword());
            existingUser.setEncryptedPassword(encryptedPassword);
        }

        log.info("New Existing User before saving: {}", existingUser);
        usersRepository.saveUser(existingUser);
        log.info("Successfully updated user: {}", existingUser);

        return existingUser;
    }
}
