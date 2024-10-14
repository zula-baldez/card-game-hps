--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS accounts
(
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(255),
    fines           INT,
    current_room_id BIGINT
);

CREATE TABLE IF NOT EXISTS friendships
(
    id              BIGINT PRIMARY KEY,
    from_account_id BIGINT,
    to_account_id   BIGINT,
    status          VARCHAR(255),
    UNIQUE (from_account_id, to_account_id),
    CONSTRAINT fk_friendships_from_account_id FOREIGN KEY (from_account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_friendships_to_account_id FOREIGN KEY (to_account_id) REFERENCES accounts (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS room
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    host_id         BIGINT,
    capacity        INT,
    current_game_id BIGINT
);

CREATE TABLE IF NOT EXISTS banned_players
(
    room_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (room_id, user_id)
);

CREATE SEQUENCE IF NOT EXISTS roles_id_seq;
CREATE SEQUENCE IF NOT EXISTS room_id_seq;
CREATE SEQUENCE IF NOT EXISTS account_id_seq;
CREATE SEQUENCE IF NOT EXISTS friendship_id_seq;

