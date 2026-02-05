// src/main/java/com/consoledoom/validation/Validator.java
package com.consoledoom.validation;

import java.util.function.Predicate;

/**
 * Functional interface for validators with lambda support.
 */
@FunctionalInterface
public interface Validator<T> {
    ValidationResult validate(T value);

    default Validator<T> and(Validator<T> other) {
        return value -> this.validate(value).merge(other.validate(value));
    }

    static <T> Validator<T> from(Predicate<T> predicate, String errorMessage) {
        return value -> {
            ValidationResult result = new ValidationResult();
            if (!predicate.test(value)) {
                result.addError(errorMessage);
            }
            return result;
        };
    }
}
