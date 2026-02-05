// src/main/java/com/consoledoom/validation/GameDataValidator.java
package com.consoledoom.validation;

/**
 * Validators for game-related data using lambdas.
 */
public class GameDataValidator {

    public static final Validator<Integer> SCORE_VALIDATOR = Validator.from(
            score -> score != null && score >= 0,
            "Score must be non-negative");

    public static final Validator<Integer> KILLS_VALIDATOR = Validator.from(
            kills -> kills != null && kills >= 0,
            "Kills must be non-negative");

    public static final Validator<Integer> WAVE_VALIDATOR = Validator.from(
            wave -> wave != null && wave >= 1 && wave <= 100,
            "Wave must be between 1 and 100");

    public static final Validator<Integer> TIME_VALIDATOR = Validator.from(
            time -> time != null && time >= 0,
            "Time must be non-negative");

    // Validate game session data
    public static ValidationResult validateGameSession(int score, int kills, int wave, int time) {
        return SCORE_VALIDATOR.validate(score)
                .merge(KILLS_VALIDATOR.validate(kills))
                .merge(WAVE_VALIDATOR.validate(wave))
                .merge(TIME_VALIDATOR.validate(time));
    }
}
