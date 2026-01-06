package com.authenticator.UserProfile.Model;

import com.authenticator.Auth.Model.Users;
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

    // foreign key referencing user_profiles.id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserProfile userProfile;
}

