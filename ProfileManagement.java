package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileManagement {

    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m";   // Red
    private static final String RESET_COLOR = "\033[0m";      // Reset

    public static void createOrUpdateProfile(int userId, String name, String contact, String email, 
                                              String education, String skills, String achievements, String resumeFilePath) {
        if (!isValidProfile(name, contact, email, education, skills, achievements, resumeFilePath)) {
            return; // Validation failed, exit early
        }

        String query = "INSERT INTO user_profiles (user_id, name, contact, email, education, skills, achievements, resume_path) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                       "name = VALUES(name), contact = VALUES(contact), email = VALUES(email), " +
                       "education = VALUES(education), skills = VALUES(skills), achievements = VALUES(achievements), resume_path = VALUES(resume_path)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, contact);
            stmt.setString(4, email);
            stmt.setString(5, education);
            stmt.setString(6, skills);
            stmt.setString(7, achievements);
            stmt.setString(8, resumeFilePath); // Store resume path as text

            stmt.executeUpdate();
            System.out.println(SUCCESS_COLOR + "Profile created/updated successfully!" + RESET_COLOR);

        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error creating/updating profile: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    public static void viewProfile(int userId) {
        String query = "SELECT name, contact, email, education, skills, achievements, resume_path FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                System.out.println();
                System.out.println("===== Your Profile =====");
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Contact: " + resultSet.getString("contact"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Education: " + resultSet.getString("education"));
                System.out.println("Skills: " + resultSet.getString("skills"));
                System.out.println("Achievements: " + resultSet.getString("achievements"));
                System.out.println("Resume Path: " + resultSet.getString("resume_path"));
                System.out.println();
            } else {
                System.out.println(ERROR_COLOR + "Profile not found for user." + RESET_COLOR);
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error viewing profile: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }

    public static boolean isProfileComplete(int userId) {
        String query = "SELECT education, skills, achievements, resume_path FROM user_profiles WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String education = resultSet.getString("education");
                String skills = resultSet.getString("skills");
                String achievements = resultSet.getString("achievements");
                String resumePath = resultSet.getString("resume_path");

                return education != null && !education.trim().isEmpty() &&
                       skills != null && !skills.trim().isEmpty() &&
                       achievements != null && !achievements.trim().isEmpty() &&
                       resumePath != null && !resumePath.trim().isEmpty();
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error checking profile completeness: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isValidProfile(String name, String contact, String email, 
                                          String education, String skills, String achievements, String resumeFilePath) {
        boolean isValid = true;

        // Validate name
        if (!isValidName(name)) {
            System.out.println(ERROR_COLOR + "Name should contain only letters and spaces." + RESET_COLOR);
            isValid = false;
        }

        // Validate contact
        if (!isValidContact(contact)) {
            System.out.println(ERROR_COLOR + "Contact number should be exactly 10 digits." + RESET_COLOR);
            isValid = false;
        }

        // Validate email
        if (!isValidEmail(email)) {
            System.out.println(ERROR_COLOR + "Email must contain '@' and be in a valid format." + RESET_COLOR);
            isValid = false;
        }

        // Validate education, skills, and achievements
        if (!isValidField(education)) {
            System.out.println(ERROR_COLOR + "Education field must be completed." + RESET_COLOR);
            isValid = false;
        }

        if (!isValidField(skills)) {
            System.out.println(ERROR_COLOR + "Skills field must be completed." + RESET_COLOR);
            isValid = false;
        }

        if (!isValidField(achievements)) {
            System.out.println(ERROR_COLOR + "Achievements field must be completed." + RESET_COLOR);
            isValid = false;
        }

        // Validate resume file path
        if (resumeFilePath == null || resumeFilePath.trim().isEmpty()) {
            System.out.println(ERROR_COLOR + "Resume file path must be provided." + RESET_COLOR);
            isValid = false;
        }

        return isValid;
    }

    private static boolean isValidName(String name) {
        return name != null && name.trim().length() > 0 && name.matches("[a-zA-Z ]+");
    }

    private static boolean isValidContact(String contact) {
        return contact != null && contact.length() == 10 && contact.chars().allMatch(Character::isDigit);
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.indexOf("@") > 0 && email.indexOf("@") < email.length() - 1;
    }

    private static boolean isValidField(String field) {
        return field != null && !field.trim().isEmpty();
    }
}
