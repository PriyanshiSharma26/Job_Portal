package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserSignup {

    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m";   // Red
    private static final String RESET_COLOR = "\033[0m";      // Reset

    public static void signup(String name, String email, String phone, String password) {
        boolean isValid = true;

        // Validate name
        if (!isValidName(name)) {
            System.out.println(ERROR_COLOR + "Name should contain only letters and spaces." + RESET_COLOR);
            isValid = false;
        }

        // Validate email
        if (!isValidEmail(email)) {
            System.out.println(ERROR_COLOR + "Email must contain '@' and be in a valid format." + RESET_COLOR);
            isValid = false;
        }

        // Validate phone
        if (!isValidPhone(phone)) {
            System.out.println(ERROR_COLOR + "Phone number should be exactly 10 digits." + RESET_COLOR);
            isValid = false;
        }

        // Validate password
        if (!isValidPassword(password)) {
            System.out.println(ERROR_COLOR + "Password must be at least 8 characters long and include uppercase letters, lowercase letters, digits, and special characters." + RESET_COLOR);
            isValid = false;
        }

        if (!isValid) {
            return; // Stop execution if any validation fails
        }

        // Assume a password hashing function is used here
        String hashedPassword = password; // Replace this line with password hashing if you use a library

        String query = email.endsWith("@jobPortal.com")
                ? "INSERT INTO admins (name, email, contact, password) VALUES (?, ?, ?, ?)"
                : "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, hashedPassword); // Store hashed password

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                String userType = email.endsWith("@jobPortal.com") ? "Admin" : "User";
                System.out.println(SUCCESS_COLOR + userType + " signup successful!" + RESET_COLOR);
            } else {
                System.out.println(ERROR_COLOR + "Signup failed!" + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error during signup: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    private static boolean isValidName(String name) {
        return name != null && name.trim().length() > 0 && name.matches("[a-zA-Z ]+");
    }

    private static boolean isValidEmail(String email) {
        // Ensure email contains '@' and has at least one character before and after '@'
        return email != null && email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1;
    }

    private static boolean isValidPhone(String phone) {
        return phone != null && phone.length() == 10 && phone.chars().allMatch(Character::isDigit);
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            if (Character.isLowerCase(c)) hasLowercase = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}
