package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobManagement {
    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m"; // Red
    private static final String RESET_COLOR = "\033[0m"; // Reset

    // New method to handle validation of job fields
    private static boolean validateJobFields(String title, String company, String location, String salaryRange,
                                             String description, String experience, String status) {
        boolean hasErrors = false;

        // Validate job title
        if (title == null || title.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Job title cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate company name
        if (company == null || company.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Company name cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate location
        if (location == null || location.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Location cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate salary range
        if (salaryRange == null || salaryRange.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Salary range cannot be empty." + RESET_COLOR);
            hasErrors = true;
        } else if (!salaryRange.matches("\\d+(?:-\\d+)?")) { // Validate salary range format
            System.out.println(ERROR_COLOR + "Invalid salary range format. Use 'min-max' or 'amount'." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate description
        if (description == null || description.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Description cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate experience
        if (experience == null || experience.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Experience cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }
        
        // Validate status
        if (status == null || status.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Status cannot be empty." + RESET_COLOR);
            hasErrors = true;
        }

        return hasErrors;
    }

    public static void postJob(String title, String company, String location, String salaryRange, String description, 
                               String experience, String status) {
        // Use the new validation method
        if (validateJobFields(title, company, location, salaryRange, description, experience, status)) {
            return; // Stop execution if validation fails
        }

        String query = "INSERT INTO jobs (title, company, location, salary_range, description, admin_id, experience, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int adminId = getAdminId(AdminLogin.loggedInAdminEmail);
            preparedStatement.setString(1, title.trim());
            preparedStatement.setString(2, company.trim());
            preparedStatement.setString(3, location.trim());
            preparedStatement.setString(4, salaryRange.trim());
            preparedStatement.setString(5, description.trim());
            preparedStatement.setInt(6, adminId);
            preparedStatement.setString(7, experience.trim());
            preparedStatement.setString(8, status);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(SUCCESS_COLOR + "Job posted successfully!" + RESET_COLOR);
            } else {
                System.out.println(ERROR_COLOR + "Failed to post job." + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error posting job: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    private static int getAdminId(String adminEmail) throws SQLException {
        String query = "SELECT admin_id FROM admins WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, adminEmail);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("admin_id");
            } else {
                throw new SQLException("Admin not found for email: " + adminEmail);
            }
        }
    }

    public static void updateJob(int jobId, String title, String company, String location, String salaryRange, 
                                 String description, String experience, String status) {
        // Validate job ID first
        if (jobId <= 0) {
            System.out.println(ERROR_COLOR + "Invalid job ID." + RESET_COLOR);
            return;
        }

        // Use the new validation method
        if (validateJobFields(title, company, location, salaryRange, description, experience, status)) {
            return; // Stop execution if validation fails
        }

        String query = "UPDATE jobs SET title = ?, company = ?, location = ?, salary_range = ?, description = ?, experience = ?, status = ? WHERE job_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title.trim());
            preparedStatement.setString(2, company.trim());
            preparedStatement.setString(3, location.trim());
            preparedStatement.setString(4, salaryRange.trim());
            preparedStatement.setString(5, description.trim());
            preparedStatement.setString(6, experience.trim());
            preparedStatement.setInt(7, jobId);
            preparedStatement.setString(8, status.trim());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(SUCCESS_COLOR + "Job updated successfully!" + RESET_COLOR);
            } else {
                System.out.println(ERROR_COLOR + "Failed to update job or job does not exist." + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error updating job: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    public static void deleteJob(int jobId) {
        if (jobId <= 0) {
            System.out.println(ERROR_COLOR + "Invalid job ID." + RESET_COLOR);
            return;
        }

        String query = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, jobId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(SUCCESS_COLOR + "Job deleted successfully!" + RESET_COLOR);
            } else {
                System.out.println(ERROR_COLOR + "Failed to delete job or job does not exist." + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error deleting job: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    public static void viewApplicants(int jobId) {
        if (jobId <= 0) {
            System.out.println(ERROR_COLOR + "Invalid job ID." + RESET_COLOR);
            return;
        }

        String query = "SELECT users.user_id, users.name, users.email, user_profiles.education, user_profiles.skills, " +
                       "user_profiles.achievements, user_profiles.contact, user_profiles.resume_path, " +
                       "applications.application_date, applications.status " +
                       "FROM applications " +
                       "JOIN users ON applications.user_id = users.user_id " +
                       "JOIN user_profiles ON users.user_id = user_profiles.user_id " +
                       "WHERE applications.job_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, jobId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println(ERROR_COLOR + "No applicants found for this job." + RESET_COLOR);
                return;
            }

            while (resultSet.next()) {
                System.out.println("====== Applicant ======");
                System.out.println("Applicant ID: " + resultSet.getInt("user_id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Education: " + resultSet.getString("education"));
                System.out.println("Skills: " + resultSet.getString("skills"));
                System.out.println("Achievements: " + resultSet.getString("achievements"));
                System.out.println("Contact: " + resultSet.getString("contact"));
                System.out.println("Resume Path: " + resultSet.getString("resume_path"));
                System.out.println("Application Date: " + resultSet.getDate("application_date"));
                System.out.println("Status: " + resultSet.getString("status"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error retrieving applicants: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    public static void viewPostedJobsByAdmin() {
        String query = "SELECT job_id, title, company, location, salary_range, description, experience, status " +
                       "FROM jobs WHERE admin_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int adminId = getAdminId(AdminLogin.loggedInAdminEmail);
            preparedStatement.setInt(1, adminId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println(ERROR_COLOR + "No jobs posted by this admin." + RESET_COLOR);
                return;
            }

            while (resultSet.next()) {
                System.out.println("====== Job ======");
                System.out.println("Job ID: " + resultSet.getInt("job_id"));
                System.out.println("Title: " + resultSet.getString("title"));
                System.out.println("Company: " + resultSet.getString("company"));
                System.out.println("Location: " + resultSet.getString("location"));
                System.out.println("Salary Range: " + resultSet.getString("salary_range"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Experience: " + resultSet.getString("experience"));
                System.out.println("Status: " + resultSet.getString("status"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error retrieving posted jobs: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }
}
