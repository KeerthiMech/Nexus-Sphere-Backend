package com.authenticator.Follow.service.impl;


import com.authenticator.Follow.dto.FollowRequest;
import com.authenticator.Follow.model.Follow;
import com.authenticator.Follow.model.FollowID;
import com.authenticator.Follow.model.ReportRequest;
import com.authenticator.Follow.repository.UserFollowRepository;
import com.authenticator.Follow.service.FollowService;
import com.authenticator.UserProfile.Model.UserProfile;
import com.authenticator.UserProfile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final UserFollowRepository followRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public void follow(FollowRequest request, String followerUserId) {

        if (request == null || !StringUtils.hasText(request.getTargetUsername())) {
            throw new IllegalArgumentException("targetUserId is required");
        }

        if (!StringUtils.hasText(followerUserId)) {
            throw new IllegalArgumentException("authenticated follower user id missing");
        }

        UserProfile follower = userProfileRepository
                .findByuserId(followerUserId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        UserProfile following = userProfileRepository
                .findByusername(request.getTargetUsername())
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (follower.getUserId().equals(following.getUserId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        // Prevent duplicates
        if (followRepository.existsByFollowId_FollowerIdAndFollowId_FollowingId(
                follower.getUserId(),
                following.getUserId()
        )) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollowId(new FollowID(
                follower.getUserId(),
                following.getUserId()
        ));
        follow.setFollower(follower);
        follow.setFollowing(following);

        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollow(FollowRequest request, String followerUserId) {

        if (request == null || !StringUtils.hasText(request.getTargetUsername())) {
            throw new IllegalArgumentException("targetUserId is required");
        }

        if (!StringUtils.hasText(followerUserId)) {
            throw new IllegalArgumentException("authenticated follower user id missing");
        }

        UserProfile follower = userProfileRepository
                .findByuserId(followerUserId)
                .orElseThrow(() -> new RuntimeException("Follower user profile not found"));

        UserProfile following = userProfileRepository
                .findByusername(request.getTargetUsername())
                .orElseThrow(() -> new RuntimeException("Target user profile not found"));

        // prevent self-unfollow
        if (follower.getUserId().equals(following.getUserId())) {
            throw new IllegalArgumentException("You cannot unfollow yourself");
        }

        // check existence
        boolean exists = followRepository
                .existsByFollowId_FollowerIdAndFollowId_FollowingId(
                        follower.getUserId(),
                        following.getUserId()
                );

        if (!exists) {
            return; // nothing to delete
        }

        // build composite key
        FollowID followId = new FollowID(
                follower.getUserId(),
                following.getUserId()
        );

        followRepository.deleteById(followId);
    }

    @Override
    @Transactional
    public void removeFollower(FollowRequest request, String followingUserId) {
        if (request == null || !StringUtils.hasText(request.getTargetUsername())) {
            throw new IllegalArgumentException("targetUsername is required");
        }

        if (!StringUtils.hasText(followingUserId)) {
            throw new IllegalArgumentException("authenticated following user id missing");
        }

        // following is the authenticated user (the one who is being followed)
        UserProfile following = userProfileRepository
                .findByuserId(followingUserId)
                .orElseThrow(() -> new RuntimeException("Following user profile not found"));

        // follower is the user to be removed (provided by targetUsername)
        UserProfile follower = userProfileRepository
                .findByusername(request.getTargetUsername())
                .orElseThrow(() -> new RuntimeException("Follower user profile not found"));

        // prevent removing yourself as follower
        if (follower.getUserId().equals(following.getUserId())) {
            throw new IllegalArgumentException("You cannot remove yourself as a follower");
        }

        // check existence of the follow relation (follower -> following)
        boolean exists = followRepository
                .existsByFollowId_FollowerIdAndFollowId_FollowingId(
                        follower.getUserId(),
                        following.getUserId()
                );

        if (!exists) {
            return; // nothing to delete
        }

        // build composite key and delete
        FollowID followId = new FollowID(
                follower.getUserId(),
                following.getUserId()
        );

        followRepository.deleteById(followId);
    }

    @Override
    public void blockFollower(FollowRequest request) {
        // TODO: implement block follower logic
    }

    @Override
    public void reportFollower(ReportRequest request) {
        // TODO: implement report follower logic
    }
}
