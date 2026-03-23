package com.authenticator.posts.repository;

public interface PostCountsProjection {
    Long getLikes();
    Long getComments();
    Long getShares();
}
