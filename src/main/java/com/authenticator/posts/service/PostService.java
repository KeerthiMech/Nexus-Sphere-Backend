package com.authenticator.posts.service;

import com.authenticator.posts.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getPostsByUser(Long userId);
    PostDto createPost(PostDto postDto);
    PostDto editPost(Long postId, PostDto postDto);
    void deletePost(Long postId, String authenticatedUserId);
    void likePost(Long postId);
    void commentOnPost(Long postId, String comment);
    void sharePost(Long postId);
}

