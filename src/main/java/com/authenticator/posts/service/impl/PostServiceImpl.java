package com.authenticator.posts.service.impl;

import com.authenticator.UserProfile.repository.UserProfileRepository;
import com.authenticator.posts.dto.PostDto;
import com.authenticator.posts.dto.PostResponse;
import com.authenticator.posts.model.Post;
import com.authenticator.posts.repository.PostCountsProjection;
import com.authenticator.posts.repository.UserPostsRepository;
import com.authenticator.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserPostsRepository userPostsRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public PostResponse getPostbyId(String postId) {
        PostCountsProjection counts = userPostsRepository.findCountsForPost(postId);
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        PostResponse response = new PostResponse();
        response.setPostId(post.getPostId());
        response.setUserId(post.getUserProfile().getUserId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setLikeCount(counts.getLikes());
        response.setCommentCount(counts.getComments());
        response.setShareCount(counts.getShares());
        response.setCreatedAt(post.getCreatedAt());

        return response;
    }

    @Override
    public List<PostResponse> getPostsByUser(String userId) {
        // TODO: Implement logic to fetch posts by a specific user
        return null;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        // TODO: Implement logic to create a new post
        return null;
    }

    @Override
    public PostDto editPost(String postId, PostDto postDto) {
        // TODO: Implement logic to edit an existing post
        return null;
    }

    @Override
    public void deletePost(String postId, String authenticatedUserId) {
        // TODO: Implement logic to delete a post
    }

    @Override
    public void likePost(String userid, String postId) {
        // TODO: Implement logic to like a post
    }

    @Override
    public void commentOnPost(String userid, String postId, String comment) {
        // TODO: Implement logic to add a comment to a post
    }

    @Override
    public void sharePost(String userid, String postId, String toUserId) {
        // TODO: Implement logic to share a post
    }

    @Override
    public void unlikePost(String userid, String postId) {
        // TODO: Implement logic to unlike a post
    }

    @Override
    public void deleteComment(String userid, String postId, String commentId) {
        // TODO: Implement logic to delete a comment from a post
    }
}
