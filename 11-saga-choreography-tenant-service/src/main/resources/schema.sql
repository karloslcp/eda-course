CREATE TABLE outbox
(
    event_id     UUID      NOT NULL PRIMARY KEY,
    exchange     VARCHAR   NOT NULL,
    routing_key  VARCHAR   NOT NULL,
    payload      TEXT      NOT NULL,
    payload_type VARCHAR   NOT NULL,
    processed    BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX outbox_processed_idx ON outbox (processed, created_at);

CREATE TABLE processed_events
(
    event_id     UUID      NOT NULL PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tenants
(
    id   UUID    NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
)