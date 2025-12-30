package com.authenticator.UserProfile.service.impl;

import com.authenticator.Auth.repository.UserRepository;
import com.authenticator.UserProfile.Model.UserProfile;
import com.authenticator.UserProfile.dto.UserProfileDto;
import com.authenticator.UserProfile.repository.UserFollowRepository;
import com.authenticator.UserProfile.repository.UserPostsRepository;
import com.authenticator.UserProfile.repository.UserProfileRepository;
import com.authenticator.UserProfile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
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
        UserProfile profile = userProfileRepository.findByusername(username).orElseThrow(() -> new RuntimeException("User not found"));
        String profileId = profile.getProfileId();
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUsername(username);
        userProfileDto.setFollowersCount(userFollowRepository.countByFollowId_FollowerId(profileId));
        userProfileDto.setFollowingCount(userFollowRepository.countByFollowId_FollowingId(profileId));
        userProfileDto.setPostsCount(userPostsRepository.countByUserProfile_ProfileId(profileId));

        return userProfileDto;
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
            userProfile.setFullName(userProfileDto.getFullName());

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
            existingProfile.setFullName(userProfileDto.getFullName());
        }

        userProfileRepository.save(existingProfile);

        return "User profile updated successfully";
    }
}
