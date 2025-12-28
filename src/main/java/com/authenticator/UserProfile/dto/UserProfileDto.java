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
    private Long userId;
    private String username;
    private int followersCount;
    private int followingCount;
    private int postsCount;
}
