package com.authenticator.posts.model;

import com.authenticator.UserProfile.Model.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "posts", schema ="Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "post_id")
    private String postId;
    
    @Column(name = "profile_id", nullable = false)
    private String profileId;
    
    private String title;
    private String content;
    @Column(name = "imageUrl")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="profile_id", referencedColumnName = "profile_id", insertable = false, updatable = false)
    private UserProfile userProfile;

    @CreationTimestamp
    private Instant createdAt;
}

