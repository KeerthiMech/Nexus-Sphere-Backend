package com.authenticator.Follow.repository;

import com.authenticator.Follow.model.Follow;
import com.authenticator.Follow.model.FollowID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<Follow , FollowID> {

    boolean existsByFollowId_FollowerIdAndFollowId_FollowingId(String followerId, String followingId);
    int countByFollowId_FollowerId(String profileId);
    int countByFollowId_FollowingId(String profileId);
}
