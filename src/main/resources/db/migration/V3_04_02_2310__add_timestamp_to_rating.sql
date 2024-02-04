ALTER TABLE ratings
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE table SET created_at = NOW();

ALTER TABLE ratings
    ALTER COLUMN created_at SET NOT NULL;