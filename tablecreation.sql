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


