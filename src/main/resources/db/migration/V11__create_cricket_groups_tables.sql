CREATE TABLE cricket_groups (
    id UUID PRIMARY KEY,

    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT uk_cricket_groups_name
        UNIQUE (name)
);


CREATE TABLE cricket_group_players (
    id UUID PRIMARY KEY,

    cricket_group_id UUID NOT NULL,
    player_id UUID NOT NULL,

    active BOOLEAN NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_cricket_group_players_group
        FOREIGN KEY (cricket_group_id)
        REFERENCES cricket_groups(id),

    CONSTRAINT fk_cricket_group_players_player
        FOREIGN KEY (player_id)
        REFERENCES players(id),

    CONSTRAINT uk_cricket_group_player
        UNIQUE (cricket_group_id, player_id)
);
