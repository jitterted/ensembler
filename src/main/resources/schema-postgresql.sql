drop table if exists post_entity;
drop table if exists huddle_entity;
drop table if exists participant_entity;

create table post_entity
(
    id uuid DEFAULT gen_random_uuid() primary key,
    title varchar(120)
);

create table huddle_entity
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    local_date_time TIMESTAMP NOT NULL,
    zone_id VARCHAR(32) NOT NULL
);

create table participant_entity
(
    name VARCHAR(64) NOT NULL,
    github_username VARCHAR(64) NOT NULL,
    email VARCHAR(128),
    discord_username VARCHAR(64),
    new_to_mobbing BOOLEAN,
    huddle_entity BIGINT
);
