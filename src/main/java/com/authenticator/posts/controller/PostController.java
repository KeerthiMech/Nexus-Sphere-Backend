package com.authenticator.posts.controller;

import com.authenticator.Security.CustomPrinciple;
import com.authenticator.posts.dto.PostDto;
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
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postDto.setUserId(Long.valueOf(customPrinciple.getUserid()));
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> editPost(@PathVariable Long postId,
                                            @RequestBody PostDto postDto,
                                            @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postDto.setUserId(Long.valueOf(customPrinciple.getUserid()));
        return ResponseEntity.ok(postService.editPost(postId, postDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.deletePost(postId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId,
                                         @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.likePost(postId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.unlikePost(postId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Void> commentOnPost(@PathVariable Long postId,
                                              @RequestBody String comment,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.commentOnPost(postId, comment, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.deleteComment(postId, commentId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<Void> sharePost(@PathVariable Long postId,
                                          @AuthenticationPrincipal CustomPrinciple customPrinciple) {
        postService.sharePost(postId, customPrinciple.getUserid());
        return ResponseEntity.ok().build();
    }
}
