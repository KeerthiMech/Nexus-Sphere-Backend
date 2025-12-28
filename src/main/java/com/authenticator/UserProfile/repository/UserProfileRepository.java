package com.authenticator.UserProfile.repository;

import com.authenticator.UserProfile.Model.UserProfile;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByusername(String username);
    Optional<UserProfile> findByprofileId(String userId);
}
