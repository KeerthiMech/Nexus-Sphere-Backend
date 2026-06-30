package com.authenticator.posts.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "post_shares",
        schema = "Profile_Service",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "share_id")
    private String shareId;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String userId;

    @CreationTimestamp
    private Instant createdAt;
}
