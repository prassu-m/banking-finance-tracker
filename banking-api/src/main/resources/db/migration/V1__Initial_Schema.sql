-- V1__Initial_Schema.sql

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_user_email ON users(email);

CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_account_user ON accounts(user_id);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reference_number VARCHAR(36) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    category VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'USD',
    description VARCHAR(500),
    merchant VARCHAR(100),
    source_account_id UUID REFERENCES accounts(id),
    destination_account_id UUID REFERENCES accounts(id),
    transaction_date TIMESTAMP NOT NULL,
    reconciled BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_txn_source_account ON transactions(source_account_id);
CREATE INDEX idx_txn_destination_account ON transactions(destination_account_id);
CREATE INDEX idx_txn_created_at ON transactions(created_at);
CREATE INDEX idx_txn_reference ON transactions(reference_number);

CREATE TABLE budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    limit_amount NUMERIC(19, 4) NOT NULL,
    period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    alert_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    alert_threshold_percent INT NOT NULL DEFAULT 80,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_budget_user ON budgets(user_id);
CREATE INDEX idx_budget_category ON budgets(category);
