package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLogin {

    private static final String SUCCESS_COLOR = "\033[0;32m"; 
    private static final String ERROR_COLOR = "\033[0;31m"; 
    private static final String RESET_COLOR = "\033[0m"; 
    static String loggedInAdminEmail;

    public static boolean login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            System.out.println(ERROR_COLOR + "Error: Email or password cannot be empty!" + RESET_COLOR);
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM admins WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                loggedInAdminEmail = email;
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(SUCCESS_COLOR + "Login successful!" + RESET_COLOR);
                        return true;
                    } else {
                        System.out.println(ERROR_COLOR + "Error: Invalid email or password!" + RESET_COLOR);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Database error: " + e.getMessage() + RESET_COLOR);
            return false;
        }
    }
}
