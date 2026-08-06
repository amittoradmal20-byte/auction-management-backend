-- ===========================================================
-- Seed Authentication Master Data
-- Version : V2
-- Description : Seed Roles, Permissions and Role-Permission Mapping
-- ===========================================================

-- ===========================================================
-- PERMISSIONS
-- ===========================================================

INSERT INTO permissions (
    name,
    description,
    created_at,
    updated_at
)
VALUES

-- ==========================
-- USER PERMISSIONS
-- ==========================
('USER_CREATE', 'Create User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER_UPDATE', 'Update User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER_DELETE', 'Delete User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER_VIEW',   'View User',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ==========================
-- ROLE PERMISSIONS
-- ==========================
('ROLE_CREATE', 'Create Role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_UPDATE', 'Update Role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_DELETE', 'Delete Role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_VIEW',   'View Role',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ==========================
-- PLAYER PERMISSIONS
-- ==========================
('PLAYER_CREATE', 'Create Player', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PLAYER_UPDATE', 'Update Player', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PLAYER_DELETE', 'Delete Player', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PLAYER_VIEW',   'View Player',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ==========================
-- AUCTION PERMISSIONS
-- ==========================
('AUCTION_CREATE', 'Create Auction', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AUCTION_UPDATE', 'Update Auction', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AUCTION_DELETE', 'Delete Auction', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AUCTION_VIEW',   'View Auction',   CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ==========================
-- AI PERMISSIONS
-- ==========================
('AI_CHAT',               'Use AI Chat', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AI_BID_RECOMMENDATION', 'AI Bid Recommendation', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AI_PLAYER_ANALYSIS',    'AI Player Analysis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===========================================================
-- ROLES
-- ===========================================================

INSERT INTO roles (
    name,
    description,
    created_at,
    updated_at
)
VALUES
('ADMIN', 'System Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USER', 'Application User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AUCTION_MANAGER', 'Auction Manager', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ===========================================================
-- ADMIN -> ALL PERMISSIONS
-- ===========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN';

-- ===========================================================
-- USER ROLE PERMISSIONS
-- ===========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
JOIN permissions p
    ON p.name IN (

        'PLAYER_VIEW',

        'AUCTION_VIEW',

        'AI_CHAT',

        'USER_VIEW'
    )
WHERE r.name = 'USER';

-- ===========================================================
-- AUCTION MANAGER PERMISSIONS
-- ===========================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT
    r.id,
    p.id
FROM roles r
JOIN permissions p
    ON p.name IN (

        'PLAYER_CREATE',
        'PLAYER_UPDATE',
        'PLAYER_DELETE',
        'PLAYER_VIEW',

        'AUCTION_CREATE',
        'AUCTION_UPDATE',
        'AUCTION_DELETE',
        'AUCTION_VIEW',

        'AI_CHAT',
        'AI_BID_RECOMMENDATION',
        'AI_PLAYER_ANALYSIS'

    )
WHERE r.name = 'AUCTION_MANAGER';