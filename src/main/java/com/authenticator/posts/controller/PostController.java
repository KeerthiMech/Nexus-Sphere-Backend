package com.authenticator.posts.controller;

import com.authenticator.Security.CustomPrinciple;
import com.authenticator.posts.dto.PostDto;
import com.authenticator.posts.dto.PostResponse;
import com.authenticator.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    // New: return a single post by postId
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId) {
        return ResponseEntity.ok(postService.getPostbyId(postId));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postDto.setUserId(postDto.getUserId());
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> editPost(@PathVariable String postId,
                                            @RequestBody PostDto postDto,
                                            @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postDto.setUserId(postDto.getUserId());
        return ResponseEntity.ok(postService.editPost(postId, postDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String postId,
                                           @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.deletePost(postId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable String postId,
                                         @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.likePost(customPrinciple.getUserid(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId,
                                           @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.unlikePost(customPrinciple.getUserid(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Void> commentOnPost(@PathVariable String postId,
                                              @RequestBody String comment,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.commentOnPost(customPrinciple.getUserid(),postId, comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String postId,
                                              @PathVariable String commentId,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.deleteComment( customPrinciple.getUserid(),postId, commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/{toUserId}/share")
    public ResponseEntity<Void> sharePost(@PathVariable String postId,@PathVariable String toUserId,
                                          @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.sharePost(customPrinciple.getUserid(),postId,toUserId);
        return ResponseEntity.ok().build();
    }
}
