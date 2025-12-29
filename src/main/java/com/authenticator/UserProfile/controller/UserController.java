package com.authenticator.UserProfile.controller;

import com.authenticator.Security.CustomPrinciple;
import com.authenticator.UserProfile.dto.UserProfileDto;
import com.authenticator.UserProfile.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user")
public class UserController {
    private final UserProfileService userProfileService;

    // Constructor injection with @Qualifier
    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }


    @PostMapping("/createProfile")
    ResponseEntity<?> createUserProfile(@AuthenticationPrincipal CustomPrinciple customPrinciple, @RequestBody UserProfileDto userProfileDto){
        String userid = customPrinciple.getUserid();
        return ResponseEntity.ok(userProfileService.createProfile(userid,userProfileDto));
    }

    @PostMapping

    @GetMapping("/profile/{username}")
    ResponseEntity<?> getUserProfile(@PathVariable String username){
        return ResponseEntity.ok(userProfileService.getprofile(username));
    }

}
