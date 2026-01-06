package com.authenticator.UserProfile.repository;

import com.authenticator.UserProfile.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostsRepository extends JpaRepository<Post, Integer> {
     int countByUserProfile_userId(String userid);
}
