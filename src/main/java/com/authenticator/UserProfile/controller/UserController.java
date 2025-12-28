package com.authenticator.UserProfile.controller;


import com.authenticator.Auth.Model.Users;
import com.authenticator.UserProfile.dto.UserProfileDto;
import com.authenticator.UserProfile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping("/profile/{username}")
    ResponseEntity<?> getUserProfile(@PathVariable String username){
        return ResponseEntity.ok(userProfileService.getprofile(username));
    }

}
