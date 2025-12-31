package com.authenticator.UserProfile.dto;

import lombok.Data;

@Data
public class FollowRequest {
    private String actorProfileId;   // who initiates the action
    private String targetProfileId;  // who is being followed/unfollowed
    // add other fields (e.g., reason) if needed
}

