-- ==========================================
-- V20: PARTIAL ROUND ROBIN CONFIGURATION
-- ==========================================

-- 1. Add opponents to skip configuration

ALTER TABLE match_schedules
ADD COLUMN opponents_to_skip INTEGER NOT NULL DEFAULT 0;


-- 2. Validate non-negative skip count

ALTER TABLE match_schedules
ADD CONSTRAINT chk_match_schedules_opponents_to_skip
CHECK (opponents_to_skip >= 0);


-- 3. Validate league format and skip count

ALTER TABLE match_schedules
ADD CONSTRAINT chk_match_schedules_league_skip
CHECK (
    (
        league_format = 'PARTIAL_ROUND_ROBIN'
        AND opponents_to_skip > 0
    )
    OR
    (
        league_format <> 'PARTIAL_ROUND_ROBIN'
        AND opponents_to_skip = 0
    )
);


-- 4. Add column documentation

COMMENT ON COLUMN match_schedules.opponents_to_skip
IS 'Number of opponents each team skips during a partial round-robin league';