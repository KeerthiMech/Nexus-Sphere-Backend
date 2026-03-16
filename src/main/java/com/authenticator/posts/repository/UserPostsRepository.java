package com.authenticator.posts.repository;

import com.authenticator.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPostsRepository extends JpaRepository<Post, String> {
    int countByUserProfile_userId(String userId);
    @Query("""
SELECT COUNT(l)
FROM Like l
JOIN Post p ON l.postId = p.postId
WHERE p.postId = :postId
""")
    long countLikesWithPost(@Param("postId") Long postId);
}
