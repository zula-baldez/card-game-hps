--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255),
    password VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS roles
(
    id        BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS  user_role
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  player
(
    id               BIGINT PRIMARY KEY,
    name             VARCHAR(255),
    fines            INT,
    active           BOOLEAN,
    current_room_id BIGINT
);

CREATE TABLE IF NOT EXISTS  user_friends
(
    user_id   BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_user_friends_user FOREIGN KEY (user_id) REFERENCES player (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_friends_friend FOREIGN KEY (friend_id) REFERENCES player (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  room
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    host_id         BIGINT,
    capacity        INT,
    current_game_id BIGINT
);
CREATE SEQUENCE IF NOT EXISTS  roles_id_seq;
CREATE SEQUENCE IF NOT EXISTS  room_id_seq;
CREATE SEQUENCE IF NOT EXISTS users_id_seq;