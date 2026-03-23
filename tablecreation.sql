CREATE SCHEMA Auth_Cred;
CREATE SCHEMA Profile_Service;

CREATE TABLE Auth_Cred.users (
    user_id CHAR(36) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(75) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SET GLOBAL event_scheduler = ON;
SHOW VARIABLES LIKE 'event_scheduler';



CREATE TABLE Auth_Cred.password_reset_token (
    id CHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_time DATETIME NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE EVENT delete_expired_password_tokens
ON SCHEDULE EVERY 1 MINUTE
DO
DELETE FROM Auth_Cred.password_reset_token
WHERE expiry_time < NOW();

CREATE TABLE Profile_Service.user_profile (
    profile_id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    fullname VARCHAR(100),
    bio VARCHAR(500),
    profile_pic_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) references Auth_Cred.users(user_id)
);


CREATE TABLE Profile_Service.follow (
    follower_id VARCHAR(36) NOT NULL,
    following_id VARCHAR(36) NOT NULL,
    followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (follower_id, following_id),

    CONSTRAINT fk_follower
        FOREIGN KEY (follower_id)
        REFERENCES Profile_Service.user_profile(profile_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_following
        FOREIGN KEY (following_id)
        REFERENCES Profile_Service.user_profile(profile_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_no_self_follow
        CHECK (follower_id <> following_id)
);

CREATE TABLE Profile_Service.posts (
    post_id VARCHAR(36) PRIMARY KEY,
    profile_id VARCHAR(36) not null,
    content TEXT NOT NULL,
    imageUrl VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_user
        FOREIGN KEY (profile_id)
        REFERENCES Profile_Service.user_profile(profile_id)
        ON DELETE CASCADE
);

    CREATE TABLE Profile_Service.post_likes (
    like_id VARCHAR(100) PRIMARY KEY,
    post_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_likes_post
        FOREIGN KEY (post_id)
        REFERENCES Profile_Service.posts(post_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_likes_user
        FOREIGN KEY (user_id)
        REFERENCES Profile_Service.user_profile(user_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_post_user_like UNIQUE (post_id, user_id)
);

CREATE TABLE Profile_Service.post_comments (
    comment_id VARCHAR(36) PRIMARY KEY,
    post_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES Profile_Service.posts(post_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)
        REFERENCES Profile_Service.user_profile(user_id)
        ON DELETE CASCADE
);

CREATE TABLE Profile_Service.post_shares (
    share_id VARCHAR(36) PRIMARY KEY,
    post_id VARCHAR(36) not null,
    user_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shares_post
        FOREIGN KEY (post_id)
        REFERENCES Profile_Service.posts(post_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shares_user
        FOREIGN KEY (user_id)
        REFERENCES Profile_Service.user_profile(user_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_post_user_share UNIQUE (post_id, user_id)
);

CREATE INDEX idx_posts_user_id
ON Profile_Service.posts(user_id);

CREATE INDEX idx_likes_post_id
ON Profile_Service.post_likes(post_id);

CREATE INDEX idx_comments_post_id
ON Profile_Service.post_comments(post_id);

CREATE INDEX idx_shares_post_id
ON Profile_Service.post_shares(post_id);



