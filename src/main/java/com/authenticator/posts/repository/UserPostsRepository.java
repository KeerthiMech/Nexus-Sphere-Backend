package com.authenticator.posts.repository;

import com.authenticator.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserPostsRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT COUNT(p) FROM Post p WHERE p.postId = :postId")
    int getPostCountByPostId(String postId);

    @Query("SELECT SUM(p.likeCount) FROM Post p WHERE p.postId = :postId")
    int getLikeCountByPostId(String postId);

    @Query("SELECT SUM(p.commentCount) FROM Post p WHERE p.postId = :postId")
    int getCommentCountByPostId(String postId);

    @Query("SELECT SUM(p.shareCount) FROM Post p WHERE p.postId = :postId")
    int getShareCountByPostId(String postId);
}
