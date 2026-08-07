CREATE TABLE IF NOT EXISTS outbox
(
    message_id   UUID PRIMARY KEY,
    exchange     VARCHAR   NOT NULL,
    routing_key  VARCHAR   NOT NULL,
    payload      TEXT      NOT NULL,
    payload_type VARCHAR   NOT NULL,
    published    BOOLEAN   NOT NULL DEFAULT false,
    created_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS processed_messages
(
    message_id   UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS accounts
(
    account_id VARCHAR PRIMARY KEY,
    balance    INT NOT NULL DEFAULT 0
);
