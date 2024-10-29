--liquibase formatted sql
--changeset egorik:1

CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL,
    name            VARCHAR(255) UNIQUE,
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
    ('admin', '$2a$10$TygvXDD7sx23DJtILn2yZOAlDFDuhOO/T0lxQ2ZZ9tyHxSyh2tmji'),
    ('room-service', '$2a$10$ItpNLjKo6WV5vwTeqJ.yh.GwjipMGzPI7MF865uWbP572L1RRi7vG'),
    ('personal-account', '$2a$10$t33jE.yelsRFDxmiyjOKT.mk.DdM2xogDVrdTtyEselq2jcC7Rr06'),
    ('game-handler', '$2a$10$yUdMQdaGJV/py9.tUKN83.Hn.1qb2Y4sPhj6AInn1fUm7MAZ3aaVC'),
    ('auth-service', '$2a$10$uYlgFqhS4tqvb0w8oeaI7uUGHCiK2/aMM74MRUORmGE4OwWugaRNG');

INSERT INTO user_role
(
    SELECT users.id, service_role.id
    FROM users CROSS JOIN (SELECT roles.id as id FROM roles WHERE roles.role_name = 'SERVICE') as service_role
    WHERE name IN ('room-service', 'personal-account', 'game-handler', 'auth-service')
);

INSERT INTO user_role
(
    SELECT users.id, service_role.id
    FROM users CROSS JOIN (SELECT roles.id as id FROM roles WHERE roles.role_name IN ('ADMIN', 'USER')) as service_role
    WHERE name = 'admin'
);