--liquibase formatted sql
--changeset egorik:1


CREATE SEQUENCE IF NOT EXISTS account_id_seq;
CREATE SEQUENCE IF NOT EXISTS room_account_id_seq;
CREATE SEQUENCE IF NOT EXISTS users_id_seq;
CREATE SEQUENCE IF NOT EXISTS room_account_id_seq;
CREATE SEQUENCE IF NOT EXISTS friendship_id_seq;




CREATE TABLE IF NOT EXISTS accounts
(
    id              BIGINT PRIMARY KEY DEFAULT nextval ('account_id_seq'),
    name            VARCHAR(255) NOT NULL,
    fines           INT CHECK (fines >= 0),
    current_room_id BIGINT
);


CREATE TABLE IF NOT EXISTS friendships
(
    id              BIGINT PRIMARY KEY DEFAULT nextval ('friendship_id_seq'),
    from_account_id BIGINT       NOT NULL,
    to_account_id   BIGINT       NOT NULL,
    status          VARCHAR(255) NOT NULL,
    CONSTRAINT fk_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    CONSTRAINT fk_to_account FOREIGN KEY (to_account_id) REFERENCES accounts (id) ON DELETE CASCADE
);