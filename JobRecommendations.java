package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobRecommendations {
    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m";   // Red
    private static final String RESET_COLOR = "\033[0m";      // Reset

    public static void viewJobRecommendations(int userId) {
        if (userId <= 0) {
            System.out.println(ERROR_COLOR + "Invalid user ID." + RESET_COLOR);
            return;
        }

        // Example recommendation query: Find jobs similar to those previously applied for
        String query = "SELECT * FROM jobs WHERE job_id IN (SELECT job_id FROM applications WHERE user_id = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println(ERROR_COLOR + "No job recommendations found." + RESET_COLOR);
                return;
            }

            while (resultSet.next()) {
                System.out.println();
                System.out.println("Job ID: " + resultSet.getInt("job_id")); 
                System.out.println("Job Title: " + resultSet.getString("title"));
                System.out.println("Company: " + resultSet.getString("company"));
                System.out.println("Location: " + resultSet.getString("location"));
                System.out.println("Salary Range: " + resultSet.getString("salary_range"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error viewing job recommendations: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }
}
