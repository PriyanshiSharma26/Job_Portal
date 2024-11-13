package com.jobportal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/job_portal";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database: " + e.getMessage(), e);
        }
    }
}

class TestDatabaseConnection {
    private static final String SUCCESS_COLOR = "\033[0;32m"; 
    private static final String ERROR_COLOR = "\033[0;31m";   
    private static final String RESET_COLOR = "\033[0m";      

    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println(SUCCESS_COLOR + "Database connection successful!" + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Database connection failed: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }
}
