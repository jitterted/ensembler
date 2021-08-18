create table huddle_entity
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    date_time_utc TIMESTAMP NOT NULL
);

create table registered_members
(
    member_id BIGINT NOT NULL,
    huddle_id BIGINT NOT NULL
);

create table member_entity
(
    id SERIAL NOT NULL PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    github_username VARCHAR(64) NOT NULL
);
