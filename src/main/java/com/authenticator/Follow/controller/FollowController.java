package com.authenticator.Follow.controller;

import com.authenticator.Follow.dto.FollowRequest;
import com.authenticator.Follow.model.ReportRequest;
import com.authenticator.Follow.service.FollowService;
import com.authenticator.Security.CustomPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/f2f")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody FollowRequest request,
                                    @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        String authenticatedUserId = customPrinciple.getUserid();
        followService.follow(request, authenticatedUserId);
        return ResponseEntity.ok("follow request received");
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestBody FollowRequest request,
                                      @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        String authenticatedUserId = customPrinciple.getUserid();
        followService.unfollow(request, authenticatedUserId);
        return ResponseEntity.ok("unfollow request received");
    }

    @PostMapping("/remove-follower")
    public ResponseEntity<?> removeFollower(@RequestBody FollowRequest request,
                                            @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        String authenticatedUserId = customPrinciple.getUserid();
        followService.removeFollower(request, authenticatedUserId);
        return ResponseEntity.ok("remove follower request received");
    }

    @PostMapping("/block-follower")
    public ResponseEntity<?> blockFollower(@RequestBody FollowRequest request) {
        followService.blockFollower(request);
        return ResponseEntity.ok("block follower request received");
    }

    @PostMapping("/report-follower")
    public ResponseEntity<?> reportFollower(@RequestBody ReportRequest request) {
        followService.reportFollower(request);
        return ResponseEntity.ok("report follower request received");
    }
}
