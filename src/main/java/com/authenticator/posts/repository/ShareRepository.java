package com.authenticator.posts.repository;

import com.authenticator.posts.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, String> {
    /**
     * Find all shares for a specific post
     */
    List<Share> findByPostId(String postId);

    /**
     * Count shares for a specific post
     */
    long countByPostId(String postId);

    /**
     * Find a share record by post and user
     */
    Optional<Share> findByPostIdAndUserId(String postId, String userId);

    /**
     * Check if a user has already shared a post
     */
    boolean existsByPostIdAndUserId(String postId, String userId);

    /**
     * Find all shares by a specific user
     */
    List<Share> findByUserId(String userId);

    /**
     * Delete share by post and user
     */
    void deleteByPostIdAndUserId(String postId, String userId);

    /**
     * Delete all shares for a post (cascade is handled by database)
     */
    void deleteByPostId(String postId);
}

