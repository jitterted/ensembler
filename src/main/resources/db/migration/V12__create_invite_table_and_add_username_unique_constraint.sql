ALTER TABLE members
    ADD CONSTRAINT members_github_username_key
        UNIQUE (github_username);

CREATE TABLE invites
(
    id SERIAL NOT NULL PRIMARY KEY,
    token TEXT NOT NULL,
    github_username TEXT NOT NULL,
    date_created_utc TIMESTAMP NOT NULL,
    was_used boolean NOT NULL,
    date_used_utc TIMESTAMP
);
