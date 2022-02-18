ALTER TABLE ensembles
    ALTER is_completed TYPE text
        USING CASE WHEN is_completed=true THEN
            'COMPLETED'
        ELSE
            'SCHEDULED'
        END;

ALTER TABLE ensembles
    RENAME COLUMN is_completed
    TO state;

ALTER TABLE ensembles
    ALTER COLUMN state
    SET DEFAULT 'SCHEDULED';