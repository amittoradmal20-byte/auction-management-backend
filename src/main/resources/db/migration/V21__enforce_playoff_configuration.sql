-- ==========================================
-- V21: ENFORCE PLAYOFF CONFIGURATION
-- ==========================================

-- ==========================================
-- STEP 1: CORRECT EXISTING DRAFT CONFIGURATIONS
-- ==========================================

UPDATE match_schedules
SET qualifying_team_count = 0,
    updated_at = CURRENT_TIMESTAMP
WHERE playoff_format = 'NONE'
  AND qualifying_team_count <> 0
  AND status = 'DRAFT';

UPDATE match_schedules
SET qualifying_team_count = 4,
    updated_at = CURRENT_TIMESTAMP
WHERE playoff_format = 'TOP_4_PLAYOFF'
  AND qualifying_team_count <> 4
  AND status = 'DRAFT';

-- ==========================================
-- STEP 2: ENFORCE VALID PLAYOFF CONFIGURATION
-- ==========================================

ALTER TABLE match_schedules
ADD CONSTRAINT chk_match_schedules_playoff_configuration
CHECK (
    (playoff_format = 'NONE'
        AND qualifying_team_count = 0)

    OR

    (playoff_format = 'TOP_2_FINAL'
        AND qualifying_team_count = 2)

    OR

    (playoff_format = 'TOP_3_PLAYOFF'
        AND qualifying_team_count = 3)

    OR

    (playoff_format = 'TOP_4_SEMIFINAL'
        AND qualifying_team_count = 4)

    OR

    (playoff_format = 'TOP_4_PLAYOFF'
        AND qualifying_team_count = 4)
);

-- ==========================================
-- STEP 3: DOCUMENT THE CONSTRAINT
-- ==========================================

COMMENT ON CONSTRAINT
    chk_match_schedules_playoff_configuration
ON match_schedules
IS 'Ensures qualifying team count matches the selected playoff format';