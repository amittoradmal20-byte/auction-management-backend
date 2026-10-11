CREATE TABLE tournament_players (
    id UUID PRIMARY KEY,

    tournament_id UUID NOT NULL,
    player_id UUID NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_tournament_players_tournament
        FOREIGN KEY (tournament_id)
        REFERENCES tournaments(id),

    CONSTRAINT fk_tournament_players_player
        FOREIGN KEY (player_id)
        REFERENCES players(id),

    CONSTRAINT uk_tournament_players_tournament_player
        UNIQUE (tournament_id, player_id)
);
