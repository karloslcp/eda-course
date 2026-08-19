CREATE TABLE billing_accounts
(
    tenant_id UUID NOT NULL PRIMARY KEY,
    balance   INT  NOT NULL DEFAULT 0
);