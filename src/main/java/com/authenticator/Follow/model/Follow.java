package com.authenticator.Follow.model;


import com.authenticator.UserProfile.Model.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "follow", schema ="Profile_Service")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Follow {

    @EmbeddedId
    private FollowID followId;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name ="follower_id",referencedColumnName = "userId",nullable = false)
    private UserProfile follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name ="following_id",referencedColumnName = "userId", nullable = false)
    private UserProfile following;

}
