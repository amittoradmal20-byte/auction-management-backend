ALTER TABLE auction_players
    DROP CONSTRAINT fk_auction_players_team;

ALTER TABLE auction_players
    RENAME COLUMN sold_to_team_id
    TO sold_to_tournament_team_id;

ALTER TABLE auction_players
    ADD CONSTRAINT fk_auction_players_tournament_team
    FOREIGN KEY (sold_to_tournament_team_id)
    REFERENCES tournament_teams(id);
