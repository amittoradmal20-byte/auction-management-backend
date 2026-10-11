CREATE TABLE tournament_teams (
    id UUID PRIMARY KEY,

    tournament_id UUID NOT NULL,
    team_id UUID NOT NULL,
    owner_id UUID NOT NULL,

    initial_budget NUMERIC(12, 2) NOT NULL,
    remaining_budget NUMERIC(12, 2) NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_tournament_teams_tournament
        FOREIGN KEY (tournament_id)
        REFERENCES tournaments(id),

    CONSTRAINT fk_tournament_teams_team
        FOREIGN KEY (team_id)
        REFERENCES teams(id),

    CONSTRAINT fk_tournament_teams_owner
        FOREIGN KEY (owner_id)
        REFERENCES user_accounts(id),

    CONSTRAINT uk_tournament_teams_tournament_team
        UNIQUE (tournament_id, team_id)
);