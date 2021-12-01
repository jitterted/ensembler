ALTER TABLE huddle_entity
    RENAME TO ensemble_entity;

ALTER SEQUENCE huddle_entity_id_seq
    RENAME TO ensemble_entity_id_seq;

ALTER TABLE accepted_member
RENAME COLUMN huddle_id
TO ensemble_id;

ALTER TABLE declined_member
RENAME COLUMN huddle_id
TO ensemble_id;

