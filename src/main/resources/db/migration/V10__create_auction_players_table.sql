CREATE TABLE auction_players (
    id UUID PRIMARY KEY,

    auction_id UUID NOT NULL,
    player_id UUID NOT NULL,

    base_price NUMERIC(12, 2) NOT NULL,
    auction_status VARCHAR(30) NOT NULL,

    current_bid NUMERIC(12, 2),
    sold_price NUMERIC(12, 2),
    sold_to_team_id UUID,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_auction_players_auction
        FOREIGN KEY (auction_id)
        REFERENCES auctions(id),

    CONSTRAINT fk_auction_players_player
        FOREIGN KEY (player_id)
        REFERENCES players(id),

    CONSTRAINT fk_auction_players_team
        FOREIGN KEY (sold_to_team_id)
        REFERENCES teams(id),

    CONSTRAINT uk_auction_players_auction_player
        UNIQUE (auction_id, player_id)
);