-- ===========================================================
-- Create Refresh Tokens Table
-- Version : V3
-- Description : Enterprise Refresh Token Storage
-- ===========================================================

CREATE TABLE refresh_tokens (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Store SHA-256 hash of refresh token
    token_hash VARCHAR(255) NOT NULL UNIQUE,

    -- Token expiration
    expiry_date TIMESTAMP NOT NULL,

    -- Logout / Revocation flag
    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    -- Device information
    device_name VARCHAR(1000),

    -- Client IP Address
    ip_address VARCHAR(100),

    -- Last time this refresh token was used
    last_used_at TIMESTAMP,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    -- Relationship
    user_account_id UUID NOT NULL,

    CONSTRAINT fk_refresh_tokens_user_account
        FOREIGN KEY (user_account_id)
        REFERENCES user_accounts(id)
        ON DELETE CASCADE
);

-- ===========================================================
-- Indexes
-- ===========================================================

CREATE INDEX idx_refresh_tokens_user_account
    ON refresh_tokens(user_account_id);

CREATE INDEX idx_refresh_tokens_expiry
    ON refresh_tokens(expiry_date);

CREATE INDEX idx_refresh_tokens_revoked
    ON refresh_tokens(revoked);