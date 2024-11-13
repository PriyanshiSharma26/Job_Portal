package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class JobApplication {
    private static final String SUCCESS_COLOR = "\033[0;32m"; 
    private static final String ERROR_COLOR = "\033[0;31m";   
    private static final String RESET_COLOR = "\033[0m";      

    public static void applyForJob(int userId, int jobId, String resumePath) {
        // Validate inputs
        if (userId <= 0 || jobId <= 0) {
            System.out.println(ERROR_COLOR + "Invalid user ID or job ID." + RESET_COLOR);
            return;
        }
        if (resumePath == null || resumePath.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Resume Path cannot be empty." + RESET_COLOR);
            return;
        }

        // Check if the job is open
        if (!isJobOpen(jobId)) {
            System.out.println(ERROR_COLOR + "The job is no longer open for applications." + RESET_COLOR);
            return;
        }

        // SQL query to insert a new application
        String query = "INSERT INTO applications (user_id, job_id, application_date, status, resumePath) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set parameters for the query
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, jobId);
            preparedStatement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.setString(4, "Submitted");
            preparedStatement.setString(5, resumePath);

            // Execute the update and provide feedback
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(SUCCESS_COLOR + "Application submitted successfully!" + RESET_COLOR);
            } else {
                System.out.println(ERROR_COLOR + "Application submission failed." + RESET_COLOR);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.out.println(ERROR_COLOR + "Error during job application: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    private static boolean isJobOpen(int jobId) {
        String query = "SELECT status FROM jobs WHERE job_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, jobId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String status = resultSet.getString("status");
                    return "open".equalsIgnoreCase(status);
                } else {
                    System.out.println(ERROR_COLOR + "Job not found." + RESET_COLOR);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error checking job status: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
            return false;
        }
    }
}
