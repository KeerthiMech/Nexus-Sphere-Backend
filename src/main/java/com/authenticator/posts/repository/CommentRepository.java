package com.authenticator.posts.repository;

import com.authenticator.posts.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    long countByPostId(String postId);
    List<Comment> findByUserId(String userId);
    void deleteByPostId(String postId);
}


