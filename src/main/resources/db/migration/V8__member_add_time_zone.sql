ALTER TABLE member_entity
    ADD COLUMN time_zone TEXT NOT NULL
        DEFAULT 'Z';
