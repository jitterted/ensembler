ALTER TABLE huddle_entity
    ADD COLUMN recording_link TEXT NOT NULL
        DEFAULT '',
    ADD COLUMN is_completed boolean NOT NULL
        DEFAULT false;
