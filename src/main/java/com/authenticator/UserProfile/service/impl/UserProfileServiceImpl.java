package com.authenticator.UserProfile.service.impl;

import com.authenticator.Auth.repository.UserRepository;
import com.authenticator.UserProfile.Model.UserProfile;
import com.authenticator.UserProfile.dto.UserProfileDto;
import com.authenticator.Follow.repository.UserFollowRepository;
import com.authenticator.posts.repository.UserPostsRepository;
import com.authenticator.UserProfile.repository.UserProfileRepository;
import com.authenticator.UserProfile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserPostsRepository userPostsRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    @Override
    public UserProfileDto getprofile(String username) {

        UserProfile profile = userProfileRepository.findByusername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userId = profile.getUserId();

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(profile.getUsername());
        dto.setBio(profile.getBio());
        dto.setFullName(profile.getFullname());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());

        dto.setFollowersCount(
                userFollowRepository.countByFollowId_FollowingId(userId)
        );

        dto.setFollowingCount(
                userFollowRepository.countByFollowId_FollowerId(userId)
        );

        dto.setPostsCount(
                userPostsRepository.countByUserProfile_userId(userId)
        );

        return dto;
    }


    @Override
    public String createProfile(String userid, UserProfileDto userProfileDto) {
        try {
            if (userProfileRepository.findByuserId(userid).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already exists for this user");
            }

            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(userid);
            userProfile.setUsername(userProfileDto.getUsername());
            userProfile.setBio(userProfileDto.getBio());
            userProfile.setProfilePictureUrl(userProfileDto.getProfilePictureUrl());
            userProfile.setFullname(userProfileDto.getFullName());

            userProfileRepository.save(userProfile);

            return "User profile created successfully";
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed in registering user", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred... during user registration", e);
        }

    }

    @Override
    public String updateProfile(String userId, UserProfileDto userProfileDto) {

        UserProfile existingProfile = userProfileRepository
                .findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found"
                ));

        if (userProfileDto.getUsername() != null) {
            existingProfile.setUsername(userProfileDto.getUsername());
        }

        if (userProfileDto.getBio() != null) {
            existingProfile.setBio(userProfileDto.getBio());
        }

        if (userProfileDto.getProfilePictureUrl() != null) {
            existingProfile.setProfilePictureUrl(userProfileDto.getProfilePictureUrl());
        }

        if (userProfileDto.getFullName() != null) {
            existingProfile.setFullname(userProfileDto.getFullName());
        }

        userProfileRepository.save(existingProfile);

        return "User profile updated successfully";
    }
}
