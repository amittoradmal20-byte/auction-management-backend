
-- =====================================================
-- V19: Add Match Schedule Calendar Configuration
-- =====================================================

-- 1. Store allowed weekdays
-- Example: SATURDAY,SUNDAY

ALTER TABLE match_schedules
ADD COLUMN allowed_days_of_week VARCHAR(100);


-- 2. Store manually selected match dates
-- Example: 2026-12-05,2026-12-06

ALTER TABLE match_schedules
ADD COLUMN selected_dates TEXT;


-- 3. Add column descriptions

COMMENT ON COLUMN match_schedules.allowed_days_of_week
IS 'Comma-separated weekdays allowed for match scheduling';

COMMENT ON COLUMN match_schedules.selected_dates
IS 'Comma-separated manually selected match dates';


-- =====================================================
-- End of V19 Migration
-- =====================================================
