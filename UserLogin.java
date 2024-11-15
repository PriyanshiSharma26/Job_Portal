package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLogin {
    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m";   // Red
    private static final String RESET_COLOR = "\033[0m";      // Reset
    static String loggedInEmail;
    public static boolean login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Email and password cannot be empty." + RESET_COLOR);
            return false;
        }
        loggedInEmail = email;
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println(SUCCESS_COLOR + "Login successful!" + RESET_COLOR);
                return true;
            } else {
                System.out.println(ERROR_COLOR + "Invalid email or password." + RESET_COLOR);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error during login: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
            return false;
        }
    }
}
