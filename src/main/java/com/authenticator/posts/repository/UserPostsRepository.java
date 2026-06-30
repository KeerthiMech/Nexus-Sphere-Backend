package com.authenticator.posts.repository;

import com.authenticator.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPostsRepository extends JpaRepository<Post, String> {
    @Query(value = "SELECT COUNT(*) FROM profile_service.posts WHERE profile_id = :profileId", nativeQuery = true)
    int countPostsByPerProfileId(@Param("profileId") String userId);
    @Query(value = "SELECT COUNT(p.post_id) FROM profile_service.posts p " +
            "INNER JOIN profile_service.user_profile up ON p.profile_id = up.profile_id " +
            "WHERE up.user_id = :userId", nativeQuery = true)
    int countPostsByUserId(@Param("userId") String userId);
    @Query(value = ""
            + "SELECT "
            + "  (SELECT COUNT(*) FROM profile_service.post_likes    WHERE post_id = :postId) AS likes, "
            + "  (SELECT COUNT(*) FROM profile_service.post_comments WHERE post_id = :postId) AS comments, "
            + "  (SELECT COUNT(*) FROM profile_service.post_shares   WHERE post_id = :postId) AS shares",
            nativeQuery = true)
    PostCountsProjection findCountsForPost(@Param("postId") String postId);
}
