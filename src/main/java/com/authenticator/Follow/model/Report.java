package com.authenticator.Follow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * Represents a report filed against a user
 */
@Entity
@Table(name = "reports", schema = "Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id")
    private String reportId;

    @Column(name = "reporter_id", nullable = false)
    private String reporterId;

    @Column(name = "reported_user_id", nullable = false)
    private String reportedUserId;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, REVIEWED, RESOLVED

    @CreationTimestamp
    @Column(name = "reported_at")
    private Instant reportedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
}

