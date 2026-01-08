package com.authenticator.posts.repository;

import com.authenticator.posts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostsRepository extends JpaRepository<Post, Integer> {
     int (String userid);
}
