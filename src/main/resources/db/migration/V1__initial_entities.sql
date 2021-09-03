create table huddle_entity
(
    id SERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
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
    first_name TEXT NOT NULL,
    github_username TEXT NOT NULL,
    roles TEXT[]
);
