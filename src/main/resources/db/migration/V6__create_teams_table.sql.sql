CREATE TABLE teams (
    id UUID PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20) NOT NULL,
    logo_url VARCHAR(500),

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT uk_teams_name
        UNIQUE (name),

    CONSTRAINT uk_teams_short_name
        UNIQUE (short_name)
);