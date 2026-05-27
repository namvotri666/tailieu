--liquibase formatted sql
--changeset manh:2

INSERT INTO role(id, name) VALUES (1, 'ADMIN'), (2, 'USER');