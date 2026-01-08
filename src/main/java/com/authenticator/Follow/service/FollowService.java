package com.authenticator.Follow.service;


import com.authenticator.Follow.dto.FollowRequest;
import com.authenticator.Follow.model.ReportRequest;

public interface FollowService {
    void follow(FollowRequest request, String followerUserId);
    void unfollow(FollowRequest request, String followerUserId);
    void removeFollower(FollowRequest request, String followingUserId);
    void blockFollower(FollowRequest request);
    void reportFollower(ReportRequest request);
}
