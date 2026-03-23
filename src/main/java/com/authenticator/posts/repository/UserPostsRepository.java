package com.authenticator.posts.repository;

import com.authenticator.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPostsRepository extends JpaRepository<Post, String> {
    @Query(value = "SELECT COUNT(*) FROM Profile_Service.posts WHERE userId = :userId", nativeQuery = true)
    int countPostsByPerUserId(@Param("userId") String userId);
    @Query(value = ""
            + "SELECT "
            + "  (SELECT COUNT(*) FROM User_Services.post_likes    WHERE post_id = :postId) AS likes, "
            + "  (SELECT COUNT(*) FROM User_Services.post_comments WHERE post_id = :postId) AS comments, "
            + "  (SELECT COUNT(*) FROM User_Services.post_shares   WHERE post_id = :postId) AS shares",
            nativeQuery = true)
    PostCountsProjection findCountsForPost(@Param("postId") String postId);
}
