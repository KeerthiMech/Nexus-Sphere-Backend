package com.authenticator.posts.service;

import com.authenticator.posts.dto.PostDto;
import com.authenticator.posts.dto.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse getPostbyId(String postId);
    List<PostResponse> getPostsByUser(String userId);
    PostDto createPost(PostDto postDto);
    PostDto editPost(String postId, PostDto postDto);
    void deletePost(String postId, String authenticatedUserId);
    void likePost(String userid,String postId);
    void commentOnPost(String userid,String postId, String comment);
    void sharePost(String userid,String postId,String toUserId);
    void unlikePost(String userid,String postId);
    void deleteComment(String userid,String postId, String commentId );
}

