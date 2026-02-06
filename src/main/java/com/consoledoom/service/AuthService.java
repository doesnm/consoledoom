// src/main/java/com/consoledoom/service/AuthService.java
package com.consoledoom.service;

import com.consoledoom.db.UserDAO;
import com.consoledoom.models.User;
import com.consoledoom.security.SecurityContext;
import com.consoledoom.validation.UserValidator;
import com.consoledoom.validation.ValidationResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;
    private final SecurityContext securityContext;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.securityContext = SecurityContext.getInstance();
    }

    public AuthResult register(String nickname, String password) {
        ValidationResult nicknameValidation = UserValidator.nicknameValidator().validate(nickname);
        if (!nicknameValidation.isValid()) {
            return AuthResult.failure(nicknameValidation.getFirstError());
        }

        ValidationResult passwordValidation = UserValidator.passwordValidator().validate(password);
        if (!passwordValidation.isValid()) {
            return AuthResult.failure(passwordValidation.getFirstError());
        }

        try {
            if (userDAO.nicknameExists(nickname)) {
                return AuthResult.failure("Nickname already taken");
            }

            String passwordHash = hashPassword(password);
            int userId = userDAO.registerUser(nickname, passwordHash);

            Optional<User> user = userDAO.authenticate(nickname, passwordHash);
            user.ifPresent(securityContext::setCurrentUser);

            return AuthResult.success("Registration successful", user.orElse(null));
        } catch (SQLException e) {
            return AuthResult.failure("Registration failed: " + e.getMessage());
        }
    }

    public AuthResult login(String nickname, String password) {
        if (nickname == null || nickname.isEmpty() || password == null || password.isEmpty()) {
            return AuthResult.failure("Nickname and password are required");
        }

        try {
            String passwordHash = hashPassword(password);
            Optional<User> user = userDAO.authenticate(nickname, passwordHash);

            if (user.isPresent()) {
                securityContext.setCurrentUser(user.get());
                return AuthResult.success("Login successful", user.get());
            } else {
                return AuthResult.failure("Invalid credentials or account disabled");
            }
        } catch (SQLException e) {
            return AuthResult.failure("Login failed: " + e.getMessage());
        }
    }

    public void logout() {
        securityContext.logout();
    }

    public Optional<User> getCurrentUser() {
        return securityContext.getCurrentUser();
    }

    public boolean isAuthenticated() {
        return securityContext.isAuthenticated();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        private AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public static AuthResult success(String message, User user) {
            return new AuthResult(true, message, user);
        }

        public static AuthResult failure(String message) {
            return new AuthResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }
}
