package com.authenticator.Follow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for filing a report against a user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    
    private String authenticatedUserId;      // The user filing the report
    private String reportedUsername;          // Username of the user being reported
    private String reportReason;              // Category/reason for the report
    private String description;               // Detailed description of the issue
}

