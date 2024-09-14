--liquibase formatted sql
--changeset egor:1
CREATE TABLE CLIENT_VK(
                         id INT PRIMARY KEY not null,
                         nick_name VARCHAR(50) not null,
                         access_token VARCHAR(300) not null,
                         vk_id INT not null,
                         email VARCHAR(50),
                         fines INT,
                         roles VARCHAR(50)

);
CREATE TABLE CLIENT_NEW(
                          id INT PRIMARY KEY,
                          nick_name VARCHAR(50) not null,
                          password VARCHAR(50) not null,
                          fines INT,
                          roles VARCHAR(50)

);