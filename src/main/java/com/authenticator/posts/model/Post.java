package com.authenticator.posts.model;

import com.authenticator.UserProfile.Model.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts", schema ="Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private String postId;

    private String title;
    private String content;
    private String imageUrl;
    private String userId;
}

