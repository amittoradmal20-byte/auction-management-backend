
-- =====================================================
-- V18: Match Scheduling Module
-- =====================================================

-- =====================================================
-- 1. MATCH SCHEDULES
-- =====================================================

CREATE TABLE match_schedules (

    id UUID PRIMARY KEY,

    tournament_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    scheduling_type VARCHAR(30) NOT NULL,

    league_format VARCHAR(30) NOT NULL,

    playoff_format VARCHAR(30) NOT NULL,

    qualifying_team_count INTEGER NOT NULL DEFAULT 0,

    start_date DATE NOT NULL,

    end_date DATE NOT NULL,

    daily_start_time TIME NOT NULL,

    daily_end_time TIME NOT NULL,

    match_duration_minutes INTEGER NOT NULL,

    break_between_matches_minutes INTEGER NOT NULL DEFAULT 0,

    minimum_rest_minutes INTEGER NOT NULL DEFAULT 0,

    minimum_matches_between_appearances INTEGER NOT NULL DEFAULT 1,

    max_matches_per_team_per_day INTEGER NOT NULL,

    number_of_grounds INTEGER NOT NULL DEFAULT 1,

    random_seed BIGINT NOT NULL,

    generation_number INTEGER NOT NULL DEFAULT 1,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_match_schedules_tournament
        FOREIGN KEY (tournament_id)
        REFERENCES tournaments(id),

    CONSTRAINT chk_match_schedules_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_match_schedules_times
        CHECK (daily_end_time > daily_start_time),

    CONSTRAINT chk_match_schedules_match_duration
        CHECK (match_duration_minutes > 0),

    CONSTRAINT chk_match_schedules_break
        CHECK (break_between_matches_minutes >= 0),

    CONSTRAINT chk_match_schedules_rest
        CHECK (minimum_rest_minutes >= 0),

    CONSTRAINT chk_match_schedules_match_gap
        CHECK (minimum_matches_between_appearances >= 0),

    CONSTRAINT chk_match_schedules_daily_limit
        CHECK (max_matches_per_team_per_day > 0),

    CONSTRAINT chk_match_schedules_grounds
        CHECK (number_of_grounds > 0),

    CONSTRAINT chk_match_schedules_qualifiers
        CHECK (qualifying_team_count >= 0),

    CONSTRAINT chk_match_schedules_generation
        CHECK (generation_number > 0)

);

CREATE INDEX idx_match_schedules_tournament
    ON match_schedules(tournament_id);


-- =====================================================
-- 2. MATCH SCHEDULE TEAM DRAWS
-- =====================================================

CREATE TABLE match_schedule_team_draws (

    id UUID PRIMARY KEY,

    match_schedule_id UUID NOT NULL,

    tournament_team_id UUID NOT NULL,

    random_number INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_match_schedule_draw_schedule
        FOREIGN KEY (match_schedule_id)
        REFERENCES match_schedules(id),

    CONSTRAINT fk_match_schedule_draw_team
        FOREIGN KEY (tournament_team_id)
        REFERENCES tournament_teams(id),

    CONSTRAINT uk_match_schedule_draw_team
        UNIQUE (match_schedule_id, tournament_team_id),

    CONSTRAINT uk_match_schedule_draw_number
        UNIQUE (match_schedule_id, random_number),

    CONSTRAINT chk_match_schedule_draw_number
        CHECK (random_number > 0)

);

CREATE INDEX idx_match_schedule_draw_schedule
    ON match_schedule_team_draws(match_schedule_id);


-- =====================================================
-- 3. MATCH SCHEDULE FIXTURES
-- =====================================================

CREATE TABLE match_schedule_fixtures (

    id UUID PRIMARY KEY,

    match_schedule_id UUID NOT NULL,

    match_number INTEGER NOT NULL,

    round_number INTEGER NOT NULL,

    match_stage VARCHAR(30) NOT NULL,

    home_tournament_team_id UUID,

    away_tournament_team_id UUID,

    home_team_source VARCHAR(100),

    away_team_source VARCHAR(100),

    match_date DATE NOT NULL,

    start_time TIME NOT NULL,

    end_time TIME NOT NULL,

    ground_number INTEGER NOT NULL DEFAULT 1,

    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_match_fixture_schedule
        FOREIGN KEY (match_schedule_id)
        REFERENCES match_schedules(id),

    CONSTRAINT fk_match_fixture_home_team
        FOREIGN KEY (home_tournament_team_id)
        REFERENCES tournament_teams(id),

    CONSTRAINT fk_match_fixture_away_team
        FOREIGN KEY (away_tournament_team_id)
        REFERENCES tournament_teams(id),

    CONSTRAINT uk_match_fixture_number
        UNIQUE (match_schedule_id, match_number),

    CONSTRAINT chk_match_fixture_number
        CHECK (match_number > 0),

    CONSTRAINT chk_match_fixture_round
        CHECK (round_number > 0),

    CONSTRAINT chk_match_fixture_ground
        CHECK (ground_number > 0),

    CONSTRAINT chk_match_fixture_times
        CHECK (end_time > start_time),

    CONSTRAINT chk_match_fixture_different_teams
        CHECK (
            home_tournament_team_id IS NULL
            OR away_tournament_team_id IS NULL
            OR home_tournament_team_id <> away_tournament_team_id
        )

);

CREATE INDEX idx_match_fixture_schedule
    ON match_schedule_fixtures(match_schedule_id);

CREATE INDEX idx_match_fixture_date
    ON match_schedule_fixtures(match_date);

CREATE INDEX idx_match_fixture_home_team
    ON match_schedule_fixtures(home_tournament_team_id);

CREATE INDEX idx_match_fixture_away_team
    ON match_schedule_fixtures(away_tournament_team_id);


-- =====================================================
-- 4. MATCH SCHEDULE EXCLUDED DATES
-- =====================================================

CREATE TABLE match_schedule_excluded_dates (

    id UUID PRIMARY KEY,

    match_schedule_id UUID NOT NULL,

    excluded_date DATE NOT NULL,

    reason VARCHAR(250),

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_match_excluded_date_schedule
        FOREIGN KEY (match_schedule_id)
        REFERENCES match_schedules(id),

    CONSTRAINT uk_match_excluded_date
        UNIQUE (match_schedule_id, excluded_date)

);

CREATE INDEX idx_match_excluded_date_schedule
    ON match_schedule_excluded_dates(match_schedule_id);
