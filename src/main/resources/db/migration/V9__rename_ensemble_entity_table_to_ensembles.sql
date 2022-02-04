ALTER TABLE ensemble_entity
    RENAME TO ensembles;

ALTER SEQUENCE ensemble_entity_id_seq
    RENAME TO ensembles_id_seq;

ALTER TABLE member_entity
    RENAME TO members;

ALTER SEQUENCE member_entity_id_seq
    RENAME TO members_id_seq
