-- ===========================================================
-- Authentication Schema
-- Version : V1
-- Author  : Amit
-- ===========================================================

-- ===========================================================
-- Enable UUID generation
-- ===========================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===========================================================
-- USER ACCOUNTS
-- Authentication & Authorization only
-- ===========================================================
CREATE TABLE user_accounts (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    username VARCHAR(100) NOT NULL UNIQUE,

    password VARCHAR(255) NOT NULL,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    account_locked BOOLEAN NOT NULL DEFAULT FALSE,

    account_expired BOOLEAN NOT NULL DEFAULT FALSE,

    credentials_expired BOOLEAN NOT NULL DEFAULT FALSE,

    last_login_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(255),

    updated_by VARCHAR(255)
);

-- ===========================================================
-- ROLES
-- ===========================================================
CREATE TABLE roles (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(255),

    updated_by VARCHAR(255)
);

-- ===========================================================
-- PERMISSIONS
-- ===========================================================
CREATE TABLE permissions (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name VARCHAR(100) NOT NULL UNIQUE,

    description VARCHAR(255),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(255),

    updated_by VARCHAR(255)
);

-- ===========================================================
-- USER ROLES
-- ===========================================================
CREATE TABLE user_roles (

    user_id UUID NOT NULL,

    role_id UUID NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES user_accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user
    ON user_roles(user_id);

CREATE INDEX idx_user_roles_role
    ON user_roles(role_id);

-- ===========================================================
-- ROLE PERMISSIONS
-- ===========================================================
CREATE TABLE role_permissions (

    role_id UUID NOT NULL,

    permission_id UUID NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role
    ON role_permissions(role_id);

CREATE INDEX idx_role_permissions_permission
    ON role_permissions(permission_id);