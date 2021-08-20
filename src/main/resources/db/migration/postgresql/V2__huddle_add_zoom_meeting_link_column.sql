ALTER TABLE huddle_entity
    ADD COLUMN zoom_meeting_link TEXT NOT NULL
        DEFAULT 'https://zoom.us/';
