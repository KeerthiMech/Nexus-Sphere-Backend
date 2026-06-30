package com.authenticator.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String userId;
    private String title;
    private String content;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
}

