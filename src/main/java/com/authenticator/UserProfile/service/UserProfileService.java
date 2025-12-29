package com.authenticator.UserProfile.service;

import com.authenticator.UserProfile.dto.UserProfileDto;


public interface UserProfileService {

     UserProfileDto getprofile(String username);

     String createProfile(String userid, UserProfileDto userProfileDto);
}


