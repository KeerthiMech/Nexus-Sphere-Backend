package com.authenticator.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private String postId;
    private String userId;
    private String content;
    private String imageUrl;

    private long likeCount;
    private long commentCount;
    private long shareCount;

    private Instant createdAt;
}
