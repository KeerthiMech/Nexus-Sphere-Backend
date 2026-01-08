package com.authenticator.Follow.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowID implements Serializable {

    @Column(name = "follower_id")
    private String followerId;

    @Column(name ="following_id")
    private String followingId;
}
