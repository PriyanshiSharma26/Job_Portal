package com.jobportal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JobSearch {
    private static final String SUCCESS_COLOR = "\033[0;32m"; // Green
    private static final String ERROR_COLOR = "\033[0;31m";   // Red
    private static final String RESET_COLOR = "\033[0m";      // Reset

    public static void searchJobs(String title, String location, String jobType, String company) {
        // Validate input
        if (title == null) title = "";
        if (location == null) location = "";
        if (company == null) company = "";
        if (jobType == null) jobType = "";

        // Construct query
        String query = "SELECT * FROM jobs WHERE 1=1";
        if (!title.trim().isEmpty()) {
            query += " AND title LIKE ?";
        }
        if (!location.trim().isEmpty()) {
            query += " AND location LIKE ?";
        }
        if (!company.trim().isEmpty()) {
            query += " AND company LIKE ?";
        }
        if (!jobType.trim().isEmpty()) {
            query += " AND job_type = ?";
        }
      /*  if(!experience.trim().isEmpty()){
            query+=" AND experience LIKE ?";
        }*/

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int paramIndex = 1;
            if (!title.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + title + "%");
            }
            if (!location.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + location + "%");
            }
            if (!company.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + company + "%");
            }
            if (!jobType.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%"+ jobType+"%");
            }
          /*  if(!jobType.trim().isEmpty())
            {
                stmt.setString(paramIndex++, experience);
            }*/

            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println(ERROR_COLOR + "No jobs found matching the criteria." + RESET_COLOR);
                return;
            }

            // Process result set
            while (rs.next()) {
                System.out.println("====== Search Results ======");
                System.out.println("Job ID: " + rs.getInt("job_id")); 
                System.out.println("Job Title: " + rs.getString("title"));
                System.out.println("Company: " + rs.getString("company"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("Salary Range: " + rs.getString("salary_range"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Job Type: " + rs.getString("job_type")); 
                // Added Job Type
                System.out.println("Experience required: "+rs.getString("experience"));
                System.out.println("Posted On: "+rs.getTimestamp("posted_date"));
                System.out.println("status of job :"+rs.getString("status"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(ERROR_COLOR + "Error during job search: " + e.getMessage() + RESET_COLOR);
            e.printStackTrace();
        }
    }
}


