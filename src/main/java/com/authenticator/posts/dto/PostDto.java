package com.authenticator.posts.dto;

import lombok.Data;

@Data
public class PostDto {
    private Long id;
    private Long userId;
    private String content;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
}

