CREATE TABLE tournaments (
    id UUID PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    start_date DATE,

    end_date DATE,

    status VARCHAR(40) NOT NULL,

    max_teams INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by UUID,

    updated_by UUID
);