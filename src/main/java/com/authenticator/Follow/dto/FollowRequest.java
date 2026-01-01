package com.authenticator.Follow.dto;

import lombok.Data;

@Data
public class FollowRequest {
    private String actorProfileId;   // who initiates the action
    private String targetUsername;// who is being followed/unfollowed
    // add other fields (e.g., reason) if needed
}

