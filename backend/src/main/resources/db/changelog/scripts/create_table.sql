--liquibase formatted sql
--changeset manh:1

-- ======================
-- ROLE
-- ======================
CREATE TABLE role (
                      id BIGINT PRIMARY KEY,
                      name VARCHAR(255)
);

-- ======================
-- USER
-- ======================
CREATE TABLE user (
                      id CHAR(36) PRIMARY KEY,
                      username VARCHAR(255),
                      password VARCHAR(255),
                      email VARCHAR(255),
                      first_name VARCHAR(255),
                      last_name VARCHAR(255),
                      gender VARCHAR(50),
                      enabled BOOLEAN,
                      role_id BIGINT,
                      CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- ======================
-- POST
-- ======================
CREATE TABLE post (
                      id CHAR(36) PRIMARY KEY,
                      title VARCHAR(255),
                      content TEXT,
                      like_count BIGINT,
                      create_date DATETIME,
                      warning BOOLEAN,
                      user_id CHAR(36),
                      CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- ======================
-- IMAGE
-- ======================
CREATE TABLE image (
                       id CHAR(36) PRIMARY KEY,
                       url VARCHAR(500),
                       user_id CHAR(36),
                       post_id CHAR(36),
                       CONSTRAINT fk_image_user FOREIGN KEY (user_id) REFERENCES user(id),
                       CONSTRAINT fk_image_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- ======================
-- COMMENT
-- ======================
CREATE TABLE comment (
                         id CHAR(36) PRIMARY KEY,
                         user_id CHAR(36),
                         full_name VARCHAR(255),
                         url VARCHAR(500),
                         content TEXT,
                         post_id CHAR(36),
                         CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- ======================
-- FOLLOWER
-- ======================
CREATE TABLE follower (
                          id CHAR(36) PRIMARY KEY,
                          follower_id CHAR(36),
                          user_id CHAR(36),
                          CONSTRAINT fk_follower_user FOREIGN KEY (user_id) REFERENCES user(id),
                          CONSTRAINT uk_user_follower UNIQUE (user_id, follower_id)
);

-- ======================
-- POST LIKE
-- ======================
CREATE TABLE post_like (
                           id CHAR(36) PRIMARY KEY,
                           user_id CHAR(36),
                           post_id CHAR(36),
                           create_at DATETIME,
                           CONSTRAINT fk_postlike_user FOREIGN KEY (user_id) REFERENCES user(id),
                           CONSTRAINT fk_postlike_post FOREIGN KEY (post_id) REFERENCES post(id),
                           CONSTRAINT uk_like_post_user UNIQUE (post_id, user_id)
);

-- ======================
-- REPORT
-- ======================
CREATE TABLE report (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255),
                        content TEXT,
                        post_id CHAR(36),
                        user_id CHAR(36),
                        CONSTRAINT fk_report_post FOREIGN KEY (post_id) REFERENCES post(id),
                        CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES user(id),
                        CONSTRAINT uk_report_post_user UNIQUE (post_id, user_id)
);

-- ======================
-- NOTIFICATION
-- ======================
CREATE TABLE notification (
                              id CHAR(36) PRIMARY KEY,
                              receiver_id CHAR(36),
                              sender_id CHAR(36),
                              content TEXT,
                              is_read BOOLEAN,
                              created_at DATETIME,
                              target_id CHAR(36),
                              CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES user(id),
                              CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES user(id)
);

-- ======================
-- CONTENT MODERATION
-- ======================
CREATE TABLE content_moderation (
                                    id CHAR(36) PRIMARY KEY,
                                    content_type VARCHAR(50),
                                    provider VARCHAR(255),
                                    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    post_id CHAR(36),
                                    CONSTRAINT fk_cm_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- Enum constraint (optional but recommended)
ALTER TABLE content_moderation
    ADD CONSTRAINT chk_content_type
        CHECK (content_type IN ('IMAGE', 'TEXT'));

-- ======================
-- CONTENT MODERATION RESULT
-- ======================
CREATE TABLE content_moderation_result (
                                           id CHAR(36) PRIMARY KEY,
                                           content_moderation_id CHAR(36),
                                           category VARCHAR(100),
                                           severity INT,
                                           CONSTRAINT fk_cmr_cm FOREIGN KEY (content_moderation_id) REFERENCES content_moderation(id)
);

-- ======================
-- INDEXES (IMPORTANT)
-- ======================

CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);

CREATE INDEX idx_post_user_id ON post(user_id);
CREATE INDEX idx_post_create_date ON post(create_date);
CREATE INDEX idx_post_title ON post(title);

CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_user_id ON comment(user_id);

CREATE INDEX idx_image_post_id ON image(post_id);
CREATE INDEX idx_image_user_id ON image(user_id);

CREATE INDEX idx_follower_user_id ON follower(user_id);
CREATE INDEX idx_follower_follower_id ON follower(follower_id);

CREATE INDEX idx_postlike_user_id ON post_like(user_id);
CREATE INDEX idx_postlike_post_id ON post_like(post_id);

CREATE INDEX idx_report_post_id ON report(post_id);
CREATE INDEX idx_report_user_id ON report(user_id);

CREATE INDEX idx_notification_receiver ON notification(receiver_id);
CREATE INDEX idx_notification_sender ON notification(sender_id);

CREATE INDEX idx_cm_post_id ON content_moderation(post_id);
CREATE INDEX idx_cmr_cm_id ON content_moderation_result(content_moderation_id);