package com.authenticator.Follow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite key for Block entity
 * blockerId: The user who is blocking
 * blockedId: The user being blocked
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockId implements Serializable {

    @Column(name = "blocker_id", nullable = false)
    private String blockerId;

    @Column(name = "blocked_id", nullable = false)
    private String blockedId;
}

