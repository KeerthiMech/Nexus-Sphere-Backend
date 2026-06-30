package com.authenticator.Follow.service.impl;


import com.authenticator.Follow.dto.FollowRequest;
import com.authenticator.Follow.model.Block;
import com.authenticator.Follow.model.BlockId;
import com.authenticator.Follow.model.Follow;
import com.authenticator.Follow.model.FollowID;
import com.authenticator.Follow.model.Report;
import com.authenticator.Follow.model.ReportRequest;
import com.authenticator.Follow.repository.BlockRepository;
import com.authenticator.Follow.repository.ReportRepository;
import com.authenticator.Follow.repository.UserFollowRepository;
import com.authenticator.Follow.service.FollowService;
import com.authenticator.UserProfile.Model.UserProfile;
import com.authenticator.UserProfile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final UserFollowRepository followRepository;
    private final UserProfileRepository userProfileRepository;
    private final BlockRepository blockRepository;
    private final ReportRepository reportRepository;

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

        // Check if blocked
        if (blockRepository.existsByBlockId_BlockerIdAndBlockId_BlockedId(
                following.getUserId(), followerUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You have been blocked by this user");
        }

        // Prevent duplicates
        if (followRepository.existsByFollowId_FollowerIdAndFollowId_FollowingId(
                follower.getUserId(),
                following.getUserId()
        )) {
            return;
        }

        Follow followRecord = new Follow();
        followRecord.setFollowId(new FollowID(
                follower.getUserId(),
                following.getUserId()
        ));
        followRecord.setFollower(follower);
        followRecord.setFollowing(following);

        followRepository.save(followRecord);
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
    @Transactional
    public void blockFollower(FollowRequest request) {
        // Validate input
        if (request == null || !StringUtils.hasText(request.getTargetUsername())) {
            throw new IllegalArgumentException("targetUsername is required");
        }

        if (!StringUtils.hasText(request.getAuthenticatedUserId())) {
            throw new IllegalArgumentException("Authenticated user ID is required");
        }

        String blockerId = request.getAuthenticatedUserId();
        String targetUsername = request.getTargetUsername();

        // Get blocker profile
        UserProfile blocker = userProfileRepository.findByuserId(blockerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocker profile not found"));

        // Get blocked user profile
        UserProfile blockedUser = userProfileRepository.findByusername(targetUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to block not found"));

        // Prevent self-blocking
        if (blockerId.equals(blockedUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot block yourself");
        }

        try {
            // Check if already blocked
            if (blockRepository.existsByBlockId_BlockerIdAndBlockId_BlockedId(blockerId, blockedUser.getUserId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already blocked");
            }

            // Create block record
            Block block = new Block();
            block.setBlockId(new BlockId(blockerId, blockedUser.getUserId()));
            block.setReason(request.getReason() != null ? request.getReason() : "User blocked");

            blockRepository.save(block);

            // Remove follow relationship if exists (both directions)
            FollowID followId1 = new FollowID(blockerId, blockedUser.getUserId());
            FollowID followId2 = new FollowID(blockedUser.getUserId(), blockerId);

            try {
                followRepository.deleteById(followId1);
                followRepository.deleteById(followId2);
            } catch (Exception e) {
                // Ignore if follow relationships don't exist
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to block user", e);
        }
    }

    @Override
    @Transactional
    public void reportFollower(ReportRequest request) {
        // Validate input
        if (request == null || !StringUtils.hasText(request.getReportedUsername())) {
            throw new IllegalArgumentException("Reported username is required");
        }

        if (!StringUtils.hasText(request.getReportReason())) {
            throw new IllegalArgumentException("Report reason is required");
        }

        if (!StringUtils.hasText(request.getAuthenticatedUserId())) {
            throw new IllegalArgumentException("Authenticated user ID is required");
        }

        String reporterId = request.getAuthenticatedUserId();
        String reportedUsername = request.getReportedUsername();
        String reason = request.getReportReason();

        // Get reporter profile
        UserProfile reporter = userProfileRepository.findByuserId(reporterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter profile not found"));

        // Get reported user profile
        UserProfile reportedUser = userProfileRepository.findByusername(reportedUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to report not found"));

        // Prevent self-reporting
        if (reporterId.equals(reportedUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot report yourself");
        }

        try {
            // Check for duplicate reports (prevent spam)
            if (reportRepository.existsByReporterIdAndReportedUserIdAndReason(reporterId, reportedUser.getUserId(), reason)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reported this user with this reason");
            }

            // Create report record
            Report report = new Report();
            report.setReportId(UUID.randomUUID().toString());
            report.setReporterId(reporterId);
            report.setReportedUserId(reportedUser.getUserId());
            report.setReason(reason);
            report.setDescription(request.getDescription());
            report.setStatus("PENDING");

            reportRepository.save(report);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to report user", e);
        }
    }
}
