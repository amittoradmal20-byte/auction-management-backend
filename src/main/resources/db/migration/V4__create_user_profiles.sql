-- ===========================================================
-- User Profiles
-- Version : V4
-- Description : User Personal Information
-- ===========================================================

CREATE TABLE user_profiles (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Personal Information
    first_name VARCHAR(100),

    last_name VARCHAR(100),

    email VARCHAR(150) UNIQUE,

    phone VARCHAR(20),

    date_of_birth DATE,

    gender VARCHAR(30),

    profile_image VARCHAR(255),

    address VARCHAR(500),

    -- One-to-One Relationship
    user_account_id UUID NOT NULL UNIQUE,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(255),

    updated_by VARCHAR(255),

    CONSTRAINT fk_user_profiles_user_account
        FOREIGN KEY (user_account_id)
        REFERENCES user_accounts(id)
        ON DELETE CASCADE
);

-- ===========================================================
-- Indexes
-- ===========================================================

-- No additional indexes required.
-- PostgreSQL automatically creates a unique index for:
-- 1. email
-- 2. user_account_id