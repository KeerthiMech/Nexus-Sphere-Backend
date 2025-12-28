package com.authenticator.UserProfile.service.impl;

import com.authenticator.Auth.Model.Users;
import com.authenticator.Auth.repository.UserRepository;
import com.authenticator.UserProfile.Model.UserProfile;
import com.authenticator.UserProfile.dto.UserProfileDto;
import com.authenticator.UserProfile.repository.UserFollowRepository;
import com.authenticator.UserProfile.repository.UserPostsRepository;
import com.authenticator.UserProfile.repository.UserProfileRepository;
import com.authenticator.UserProfile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserPostsRepository userPostsRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    @Override
    public UserProfileDto getprofile(String username) {
            UserProfile profile = userProfileRepository.findByusername(username).orElseThrow(()-> new RuntimeException("User not found"));
            String profileId = profile.getProfileId();
            UserProfileDto userProfileDto = new UserProfileDto();
            userProfileDto.setUsername(username);
            userProfileDto.setFollowersCount(userFollowRepository.countByFollowId_FollowerId(profileId));
            userProfileDto.setFollowingCount(userFollowRepository.countByFollowId_FollowingId(profileId));
            userProfileDto.setPostsCount(userPostsRepository.countByUserProfile_ProfileId(profileId));

            return userProfileDto;
    }

}


