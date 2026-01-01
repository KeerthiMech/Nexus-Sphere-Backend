package com.authenticator.Follow.model;

public class ReportRequest {
    private Long reporterUserId;
    private Long reportedUserId;
    private String reason;

    // ...existing code...

    public ReportRequest() {}

    public Long getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(Long reporterUserId) { this.reporterUserId = reporterUserId; }

    public Long getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

