-- ============================================================
-- V1__backup_payroll_runs.sql
-- Step 1 of 2: rename the old table so its data is preserved
-- during the migration window.
--
-- Run this BEFORE deploying the new application code.
-- The old /api/v1/admin/payroll endpoints stay in place via
-- the unchanged PayrollRunController until you are ready to
-- cut over — they still read/write payroll_runs_backup.
-- ============================================================

-- Rename old table to a backup (non-destructive)
RENAME TABLE payroll_runs TO payroll_runs_backup;

-- Add a migration_note column so you can track which rows
-- were imported into the new schema later.
ALTER TABLE payroll_runs_backup
    ADD COLUMN migrated_record_id BIGINT NULL COMMENT 'FK to payroll_records.id once migrated';
