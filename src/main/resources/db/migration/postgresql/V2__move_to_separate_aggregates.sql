create table post_entity
(
    id uuid DEFAULT gen_random_uuid() primary key,
    title varchar(120)
);

create table huddle_entity
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    date_time_utc TIMESTAMP NOT NULL
);

create table registered_members
(
    member_id BIGINT NOT NULL,
    huddle_entity BIGINT NOT NULL
);

create table member_entity
(
    member_id BIGINT NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    github_username VARCHAR(64) NOT NULL
);
