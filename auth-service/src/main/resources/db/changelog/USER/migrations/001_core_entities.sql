--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS users
(
    id              BIGINT,
    name            VARCHAR(255),
    password        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS roles
(
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);


CREATE SEQUENCE IF NOT EXISTS roles_id_seq;
CREATE SEQUENCE IF NOT EXISTS users_id_seq;