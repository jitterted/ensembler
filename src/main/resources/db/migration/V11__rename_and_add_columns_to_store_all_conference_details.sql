ALTER TABLE ensembles
    RENAME COLUMN zoom_meeting_link
        TO conference_join_url;

ALTER TABLE ensembles
    ADD COLUMN conference_meeting_id TEXT NOT NULL
        DEFAULT '';

ALTER TABLE ensembles
    ADD COLUMN conference_start_url TEXT NOT NULL
        DEFAULT '';
