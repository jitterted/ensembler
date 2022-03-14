ALTER TABLE members
    ADD CONSTRAINT members_github_username_key
        UNIQUE (github_username);

CREATE TABLE invites
(
    invite_id BIGINT NOT NULL PRIMARY KEY,
    github_username TEXT NOT NULL,
    date_created_utc TIMESTAMP NOT NULL,
    was_used boolean NOT NULL,
    date_used_utc TIMESTAMP NOT NULL
);
