--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS room
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    host_id         BIGINT,
    capacity        INT,
    current_game_id BIGINT
);

CREATE TABLE IF NOT EXISTS account_room
(
    account_id BIGINT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    CONSTRAINT fk_account_room FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS banned_accounts
(
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    CONSTRAINT fk_banned_players_room FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS room_id_seq;