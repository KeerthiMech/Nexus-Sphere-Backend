package com.authenticator.UserProfile.Model;

import com.authenticator.Follow.model.Follow;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userProfile", schema ="Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profileId", updatable = false, nullable = false)
    private String profileId;

    @Column(name ="userId", unique = true, nullable = false)
    private String userId;

    @Column(name ="username")
    private String username;

    @Column(name ="fullName")
    private String fullName;

    @Column(name ="bio", length = 500)
    private String bio;

    @Column(name ="profile_pic_url")
    private String profilePictureUrl;


    // One-to-many relationship to Post; Post holds the FK (user_profile_id)
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Follow> follower = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Follow> following = new ArrayList<>();
}
