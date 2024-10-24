--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL,
    name            VARCHAR(255),
    password        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS roles
(
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO roles (role_name)
VALUES
    ('ADMIN'),
    ('USER'),
    ('SERVICE');


INSERT INTO users (name, password)
VALUES
    ('room-service', '$2a$10$ItpNLjKo6WV5vwTeqJ.yh.GwjipMGzPI7MF865uWbP572L1RRi7vG'),
    ('personal-account', '$2a$10$t33jE.yelsRFDxmiyjOKT.mk.DdM2xogDVrdTtyEselq2jcC7Rr06'),
    ('game-handler', '$2a$10$yUdMQdaGJV/py9.tUKN83.Hn.1qb2Y4sPhj6AInn1fUm7MAZ3aaVC');

