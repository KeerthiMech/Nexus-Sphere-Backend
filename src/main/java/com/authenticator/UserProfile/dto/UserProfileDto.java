package com.authenticator.UserProfile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private String userId;
    private String username;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private String bio;
    private String ProfilePictureUrl;
    private String FullName;
}
