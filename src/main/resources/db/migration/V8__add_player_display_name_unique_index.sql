CREATE UNIQUE INDEX uk_players_display_name_lower
ON players (LOWER(display_name));