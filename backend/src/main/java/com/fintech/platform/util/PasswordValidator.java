package com.fintech.platform.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PasswordValidator {

    private static final Set<String> COMMON_PASSWORDS = new HashSet<>(Arrays.asList(
            "password", "123456", "123456789", "12345678", "12345", "1234567",
            "password123", "admin", "welcome", "monkey", "login", "abc123",
            "qwerty", "letmein", "1234567890", "Password1", "Password123"
    ));

    public static ValidationResult validate(String password, String email,
                                            String firstName, String lastName) {
        ValidationResult result = new ValidationResult();

        // Check minimum length
        if (password.length() < 12) {
            result.addError("Password must be at least 12 characters long");
            return result;
        }

        // Check for common passwords
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            result.addError("This password is too common. Please choose a more unique password");
            return result;
        }

        // Check for personal information
        String lowerPassword = password.toLowerCase();
        if (email != null && lowerPassword.contains(email.split("@")[0].toLowerCase())) {
            result.addError("Password cannot contain your email address");
            return result;
        }
        if (firstName != null && lowerPassword.contains(firstName.toLowerCase())) {
            result.addError("Password cannot contain your first name");
            return result;
        }
        if (lastName != null && lowerPassword.contains(lastName.toLowerCase())) {
            result.addError("Password cannot contain your last name");
            return result;
        }

        // Check for repetitive characters (aaaaaa, 111111)
        if (password.matches("(.)\\1{5,}")) {
            result.addError("Password cannot contain more than 5 repeated characters");
            return result;
        }

        // Check complexity (at least 3 out of 4 types)
        int complexity = 0;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        if (hasUpperCase) complexity++;
        if (hasLowerCase) complexity++;
        if (hasNumber) complexity++;
        if (hasSpecialChar) complexity++;

        if (complexity < 3) {
            result.addError("Password must contain at least 3 of the following: uppercase letters, lowercase letters, numbers, special characters");
            return result;
        }

        result.setValid(true);
        return result;
    }

    public static class ValidationResult {
        private boolean valid = false;
        private String error;

        public void addError(String error) {
            this.error = error;
            this.valid = false;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getError() {
            return error;
        }
    }
}