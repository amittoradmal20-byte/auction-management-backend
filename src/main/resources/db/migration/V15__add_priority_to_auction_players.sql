ALTER TABLE auction_players
    ADD COLUMN priority INTEGER NOT NULL DEFAULT 1000;

CREATE INDEX idx_auction_players_auction_priority
    ON auction_players (auction_id, priority);
