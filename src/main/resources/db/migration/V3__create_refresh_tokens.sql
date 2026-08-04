-- ===========================================================
-- Create Refresh Tokens Table
-- Version : V3
-- Description : Enterprise Refresh Token Storage
-- ===========================================================

CREATE TABLE refresh_tokens
(
    id BIGSERIAL PRIMARY KEY,

    -- Store SHA-256 hash of refresh token
    token_hash VARCHAR(255) NOT NULL UNIQUE,

    -- Token expiration
    expiry_date TIMESTAMP NOT NULL,

    -- Logout / Revocation flag
    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    -- Device information
    device_name VARCHAR(255),

    -- Client IP Address
    ip_address VARCHAR(100),

    -- Last time this refresh token was used
    last_used_at TIMESTAMP,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Relationship
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

--------------------------------------------------------------
-- Indexes
--------------------------------------------------------------

CREATE INDEX idx_refresh_token_hash
ON refresh_tokens(token_hash);

CREATE INDEX idx_refresh_user
ON refresh_tokens(user_id);

CREATE INDEX idx_refresh_expiry
ON refresh_tokens(expiry_date);

CREATE INDEX idx_refresh_revoked
ON refresh_tokens(revoked);