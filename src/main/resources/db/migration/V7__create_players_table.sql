CREATE TABLE players (
    id UUID PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    display_name VARCHAR(150) NOT NULL,

    date_of_birth DATE,

    nationality VARCHAR(50),

    profile_image_url VARCHAR(500),

    role VARCHAR(30) NOT NULL,
    batting_style VARCHAR(30) NOT NULL,
    bowling_style VARCHAR(40) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID
);