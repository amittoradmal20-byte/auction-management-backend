CREATE TABLE auction_bids (
    id UUID PRIMARY KEY,

    auction_player_id UUID NOT NULL,
    tournament_team_id UUID NOT NULL,

    bid_amount NUMERIC(12, 2) NOT NULL,
    bid_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_auction_bids_auction_player
        FOREIGN KEY (auction_player_id)
        REFERENCES auction_players (id),

    CONSTRAINT fk_auction_bids_tournament_team
        FOREIGN KEY (tournament_team_id)
        REFERENCES tournament_teams (id),

    CONSTRAINT chk_auction_bids_amount_positive
        CHECK (bid_amount > 0)
);

CREATE INDEX idx_auction_bids_player_time
    ON auction_bids (auction_player_id, bid_time DESC);

CREATE INDEX idx_auction_bids_team
    ON auction_bids (tournament_team_id);
