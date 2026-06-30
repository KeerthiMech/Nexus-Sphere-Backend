package com.authenticator.Follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {
    private String authenticatedUserId;      // Current authenticated user's ID
    private String actorProfileId;           // Who initiates the action (usually same as authenticatedUserId)
    private String targetUsername;           // Who is being followed/unfollowed/blocked
    private String reason;                   // Optional reason (for blocking)
}

