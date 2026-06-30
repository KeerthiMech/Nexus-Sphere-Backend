package com.authenticator.posts.repository;

import com.authenticator.posts.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, String> {
    List<Like> findByPostId(String postId);
    long countByPostId(String postId);
    Optional<Like> findByPostIdAndUserId(String postId, String userId);
    boolean existsByPostIdAndUserId(String postId, String userId);
    void deleteByPostIdAndUserId(String postId, String userId);
    List<Like> findByUserId(String userId);
    void deleteByPostId(String postId);
}


