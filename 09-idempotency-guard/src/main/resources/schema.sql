CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR PRIMARY KEY,
    balance INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);