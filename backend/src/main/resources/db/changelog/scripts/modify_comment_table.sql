--liquibase formatted sql
--changeset manh:4


ALTER TABLE comment
    ADD COLUMN create_date DATETIME;