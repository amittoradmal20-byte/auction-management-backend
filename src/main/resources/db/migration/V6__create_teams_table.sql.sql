CREATE TABLE teams (
    id UUID PRIMARY KEY,

    tournament_id UUID NOT NULL,
    owner_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20) NOT NULL,
    logo_url VARCHAR(500),

    initial_budget NUMERIC(12, 2) NOT NULL,
    remaining_budget NUMERIC(12, 2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_teams_tournament
        FOREIGN KEY (tournament_id)
        REFERENCES tournaments(id),

    CONSTRAINT fk_teams_owner
        FOREIGN KEY (owner_id)
        REFERENCES user_accounts(id),

    CONSTRAINT uk_teams_tournament_name
        UNIQUE (tournament_id, name),

    CONSTRAINT uk_teams_tournament_short_name
        UNIQUE (tournament_id, short_name)
);