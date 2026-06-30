package com.authenticator.posts.service.impl;

import com.authenticator.UserProfile.repository.UserProfileRepository;
import com.authenticator.posts.dto.PostDto;
import com.authenticator.posts.dto.PostResponse;
import com.authenticator.posts.model.Comment;
import com.authenticator.posts.model.Like;
import com.authenticator.posts.model.Post;
import com.authenticator.posts.model.Share;
import com.authenticator.posts.repository.CommentRepository;
import com.authenticator.posts.repository.LikeRepository;
import com.authenticator.posts.repository.PostCountsProjection;
import com.authenticator.posts.repository.ShareRepository;
import com.authenticator.posts.repository.UserPostsRepository;
import com.authenticator.posts.service.PostService;
import com.authenticator.UserProfile.Model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserPostsRepository userPostsRepository;
    private final UserProfileRepository userProfileRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ShareRepository shareRepository;

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
        // Verify user exists
        UserProfile userProfile = userProfileRepository.findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Get all posts for this user's profile
        List<Post> posts = userPostsRepository.findAll().stream()
                .filter(post -> post.getUserProfile().getUserId().equals(userId))
                .collect(Collectors.toList());

        // Convert to PostResponse with counts
        return posts.stream()
                .map(post -> {
                    PostCountsProjection counts = userPostsRepository.findCountsForPost(post.getPostId());
                    PostResponse response = new PostResponse();
                    response.setPostId(post.getPostId());
                    response.setUserId(userId);
                    response.setContent(post.getContent());
                    response.setImageUrl(post.getImageUrl());
                    response.setLikeCount(counts.getLikes());
                    response.setCommentCount(counts.getComments());
                    response.setShareCount(counts.getShares());
                    response.setCreatedAt(post.getCreatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDto createPost(PostDto postDto) {
        // Validate input
        if (postDto == null || postDto.getUserId() == null || postDto.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and content are required");
        }

        if (postDto.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post content cannot be empty");
        }

        if (postDto.getContent().length() > 5000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post content exceeds maximum length (5000 characters)");
        }

        // Get user profile
        UserProfile userProfile = userProfileRepository.findByuserId(postDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        try {
            // Create new post
            Post post = new Post();
            post.setPostId(UUID.randomUUID().toString());
            post.setProfileId(userProfile.getProfileId());
            post.setContent(postDto.getContent());
            post.setTitle(postDto.getTitle());
            post.setUserProfile(userProfile);

            Post savedPost = userPostsRepository.save(post);

            // Return DTO
            PostDto response = new PostDto();
            response.setUserId(postDto.getUserId());
            response.setContent(savedPost.getContent());
            response.setLikesCount(0);
            response.setCommentsCount(0);
            response.setSharesCount(0);

            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create post", e);
        }
    }

    @Override
    @Transactional
    public PostDto editPost(String postId, PostDto postDto) {
        // Validate input
        if (postDto == null || postDto.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        if (postDto.getContent() != null && postDto.getContent().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post content cannot be empty");
        }

        if (postDto.getContent() != null && postDto.getContent().length() > 5000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post content exceeds maximum length");
        }

        // Get post
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify ownership
        if (!post.getUserProfile().getUserId().equals(postDto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own posts");
        }

        try {
            // Update post
            if (postDto.getContent() != null) {
                post.setContent(postDto.getContent());
            }
            if (postDto.getTitle() != null) {
                post.setTitle(postDto.getTitle());
            }

            Post updatedPost = userPostsRepository.save(post);

            PostDto response = new PostDto();
            response.setUserId(postDto.getUserId());
            response.setContent(updatedPost.getContent());
            response.setLikesCount((int) likeRepository.countByPostId(postId));
            response.setCommentsCount((int) commentRepository.countByPostId(postId));
            response.setSharesCount((int) shareRepository.countByPostId(postId));

            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to edit post", e);
        }
    }

    @Override
    @Transactional
    public void deletePost(String postId, String authenticatedUserId) {
        // Validate input
        if (postId == null || authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID and User ID are required");
        }

        // Get post
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify ownership
        if (!post.getUserProfile().getUserId().equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
        }

        try {
            // Delete all associated comments
            commentRepository.deleteByPostId(postId);
            
            // Delete all associated likes
            likeRepository.deleteByPostId(postId);
            
            // Delete all associated shares
            shareRepository.deleteByPostId(postId);
            
            // Delete the post
            userPostsRepository.deleteById(postId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete post", e);
        }
    }

    @Override
    @Transactional
    public void likePost(String userId, String postId) {
        // Validate input
        if (userId == null || postId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Post ID are required");
        }

        // Verify post exists
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify user exists
        UserProfile userProfile = userProfileRepository.findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Check if already liked
            if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post already liked by this user");
            }

            // Create like
            Like like = new Like();
            like.setLikeId(UUID.randomUUID().toString());
            like.setPostId(postId);
            like.setUserId(userId);

            likeRepository.save(like);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to like post", e);
        }
    }

    @Override
    @Transactional
    public void commentOnPost(String userId, String postId, String comment) {
        // Validate input
        if (userId == null || postId == null || comment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID, Post ID, and comment are required");
        }

        if (comment.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment cannot be empty");
        }

        if (comment.length() > 1000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment exceeds maximum length (1000 characters)");
        }

        // Verify post exists
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify user exists
        UserProfile userProfile = userProfileRepository.findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Create comment
            Comment newComment = new Comment();
            newComment.setCommentId(UUID.randomUUID().toString());
            newComment.setPostId(postId);
            newComment.setUserId(userId);
            newComment.setComment(comment);

            commentRepository.save(newComment);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add comment", e);
        }
    }

    @Override
    @Transactional
    public void sharePost(String userId, String postId, String toUserId) {
        // Validate input
        if (userId == null || postId == null || toUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID, Post ID, and target user ID are required");
        }

        // Verify post exists
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify sharing user exists
        UserProfile sharingUser = userProfileRepository.findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sharing user not found"));

        // Verify target user exists
        UserProfile targetUser = userProfileRepository.findByuserId(toUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found"));

        // Prevent self-sharing
        if (userId.equals(toUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot share post with yourself");
        }

        try {
            // Check if already shared by this user (to prevent duplicates)
            if (shareRepository.existsByPostIdAndUserId(postId, userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post already shared by this user");
            }

            // Create share
            Share share = new Share();
            share.setShareId(UUID.randomUUID().toString());
            share.setPostId(postId);
            share.setUserId(userId);

            shareRepository.save(share);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to share post", e);
        }
    }

    @Override
    @Transactional
    public void unlikePost(String userId, String postId) {
        // Validate input
        if (userId == null || postId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Post ID are required");
        }

        // Verify post exists
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Verify user exists
        UserProfile userProfile = userProfileRepository.findByuserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            // Check if like exists
            if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not liked this post");
            }

            // Delete like
            likeRepository.deleteByPostIdAndUserId(postId, userId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to unlike post", e);
        }
    }

    @Override
    @Transactional
    public void deleteComment(String userId, String postId, String commentId) {
        // Validate input
        if (userId == null || postId == null || commentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID, Post ID, and Comment ID are required");
        }

        // Verify post exists
        Post post = userPostsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Get and verify comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        // Verify comment belongs to the post
        if (!comment.getPostId().equals(postId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to this post");
        }

        // Verify ownership - only comment author or post owner can delete
        if (!comment.getUserId().equals(userId) && !post.getUserProfile().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments");
        }

        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete comment", e);
        }
    }
}
