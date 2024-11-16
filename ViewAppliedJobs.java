package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewAppliedJobs {
    private static final String SUCCESS_COLOR = "\033[0;32m"; 
    private static final String ERROR_COLOR = "\033[0;31m";   
    private static final String RESET_COLOR = "\033[0m";      


    static class JobApplication {
        private int applicationId;
        private int jobId;
        private String applicationDate;
        private String status;

        public JobApplication(int applicationId, int jobId, String applicationDate, String status) {
            this.applicationId = applicationId;
            this.jobId = jobId;
            this.applicationDate = applicationDate;
            this.status = status;
        }

        @Override
        public String toString() {
            return "Application ID: " + applicationId + "\n" +
                   "Job ID: " + jobId + "\n" +
                   "Application Date: " + applicationDate + "\n" +
                   "Status: " + status + "\n";
        }
    }

    public static void viewAppliedJobs(int userId) {
        String query = "SELECT * FROM applications WHERE user_id = ?";
        List<JobApplication> appliedJobs = new ArrayList<>(); // Using a List to store the job applications

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int applicationId = resultSet.getInt("application_id");
                int jobId = resultSet.getInt("job_id");
                String applicationDate = resultSet.getDate("application_date").toString();
                String status = resultSet.getString("status");


                appliedJobs.add(new JobApplication(applicationId, jobId, applicationDate, status));
            }


            if (appliedJobs.isEmpty()) {
                System.out.println(SUCCESS_COLOR + "No jobs applied." + RESET_COLOR);
            } else {
                for (JobApplication job : appliedJobs) {
                    System.out.println(job);
                }
            }

        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error viewing applied jobs: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }
}
