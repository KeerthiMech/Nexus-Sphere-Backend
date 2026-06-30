package com.authenticator.Follow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a blocked user relationship
 */
@Entity
@Table(name = "blocks", schema = "Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    @EmbeddedId
    private BlockId blockId;

    @CreationTimestamp
    @Column(name = "blocked_at")
    private Instant blockedAt;

    @Column(name = "reason", length = 255)
    private String reason;
}

