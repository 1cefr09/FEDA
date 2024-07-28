CREATE database feda;

USE feda;

CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      email VARCHAR(255),
                      role VARCHAR(20) NOT NULL,
                      is_banned BOOLEAN NOT NULL DEFAULT FALSE,
                      create_time DATETIME,
                      update_time DATETIME
);

create table post
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT comment'帖子id',
    title VARCHAR(255) NOT NULL COMMENT'帖子标题',
    content TEXT NOT NULL COMMENT'帖子内容',
    author_id BIGINT NOT NULL COMMENT'作者id',
    is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    create_time DATETIME NOT NULL COMMENT'创建时间',
    update_time DATETIME NOT NULL COMMENT'更新时间（回帖）'
)
    COMMENT'发帖';


CREATE TABLE comment
(
    id BIGINT PRIMARY KEY auto_increment COMMENT'评论id',
    post_id BIGINT NOT NULL COMMENT'帖子id',
    parent_id BIGINT null COMMENT'回复的评论id',
    content TEXT NOT NULL COMMENT'评论内容',
    author_id BIGINT NOT NULL COMMENT'作者id',
    is_banned BOOLEAN NOT NULL DEFAULT FALSE,
    create_time DATETIME NOT NULL COMMENT'创建时间'
)
    COMMENT'回帖';


CREATE TABLE admin_action (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              admin_id BIGINT NOT NULL,
                              action_type VARCHAR(255) NOT NULL,
                              target_id BIGINT NOT NULL,
                              timestamp DATETIME NOT NULL
);