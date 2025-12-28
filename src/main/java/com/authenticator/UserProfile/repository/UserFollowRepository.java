package com.authenticator.UserProfile.repository;

import com.authenticator.UserProfile.Model.Follow;
import com.authenticator.UserProfile.Model.FollowID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<Follow , FollowID> {
    int countByFollowId_FollowerId(String profileId);
    int countByFollowId_FollowingId(String profileId);
}
