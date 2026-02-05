// src/main/java/com/consoledoom/validation/UserValidator.java
package com.consoledoom.validation;

import com.consoledoom.config.GameConfig;
import java.util.regex.Pattern;

/**
 * Validators for user data using lambda expressions.
 */
public class UserValidator {
    private static final GameConfig config = GameConfig.INSTANCE;

    // Lambda-based validators
    public static final Validator<String> NICKNAME_NOT_EMPTY = Validator.from(
            nickname -> nickname != null && !nickname.trim().isEmpty(),
            "Nickname cannot be empty");

    public static final Validator<String> NICKNAME_LENGTH = Validator.from(
            nickname -> nickname != null &&
                    nickname.length() >= config.getNicknameMinLength() &&
                    nickname.length() <= config.getNicknameMaxLength(),
            String.format("Nickname must be between %d and %d characters",
                    config.getNicknameMinLength(), config.getNicknameMaxLength()));

    public static final Validator<String> NICKNAME_PATTERN = Validator.from(
            nickname -> nickname != null &&
                    Pattern.matches(config.getNicknamePattern(), nickname),
            "Nickname can only contain letters, numbers, underscores and hyphens");

    public static final Validator<String> PASSWORD_NOT_EMPTY = Validator.from(
            password -> password != null && !password.isEmpty(),
            "Password cannot be empty");

    public static final Validator<String> PASSWORD_LENGTH = Validator.from(
            password -> password != null &&
                    password.length() >= config.getPasswordMinLength() &&
                    password.length() <= config.getPasswordMaxLength(),
            String.format("Password must be between %d and %d characters",
                    config.getPasswordMinLength(), config.getPasswordMaxLength()));

    public static final Validator<String> PASSWORD_STRENGTH = Validator.from(
            password -> password != null &&
                    password.chars().anyMatch(Character::isDigit) &&
                    password.chars().anyMatch(Character::isLetter),
            "Password must contain at least one letter and one digit");

    // Combined validators
    public static Validator<String> nicknameValidator() {
        return NICKNAME_NOT_EMPTY
                .and(NICKNAME_LENGTH)
                .and(NICKNAME_PATTERN);
    }

    public static Validator<String> passwordValidator() {
        return PASSWORD_NOT_EMPTY
                .and(PASSWORD_LENGTH);
    }

    public static Validator<String> strongPasswordValidator() {
        return passwordValidator().and(PASSWORD_STRENGTH);
    }
}
