// src/main/java/com/consoledoom/validation/ValidationResult.java
package com.consoledoom.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Result of validation operations with error collection.
 */
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    public void ifValid(Runnable action) {
        if (isValid()) {
            action.run();
        }
    }

    public void ifInvalid(Consumer<List<String>> errorHandler) {
        if (!isValid()) {
            errorHandler.accept(errors);
        }
    }

    public ValidationResult merge(ValidationResult other) {
        this.errors.addAll(other.errors);
        return this;
    }
}
