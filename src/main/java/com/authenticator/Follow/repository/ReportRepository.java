package com.authenticator.Follow.repository;

import com.authenticator.Follow.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, String> {
    /**
     * Find all reports filed by a specific user
     */
    List<Report> findByReporterId(String reporterId);

    /**
     * Find all reports against a specific user
     */
    List<Report> findByReportedUserId(String reportedUserId);

    /**
     * Find all pending reports
     */
    List<Report> findByStatus(String status);

    /**
     * Count reports against a user with specific status
     */
    long countByReportedUserIdAndStatus(String reportedUserId, String status);

    /**
     * Check if a user has already reported someone
     */
    boolean existsByReporterIdAndReportedUserIdAndReason(String reporterId, String reportedUserId, String reason);
}

