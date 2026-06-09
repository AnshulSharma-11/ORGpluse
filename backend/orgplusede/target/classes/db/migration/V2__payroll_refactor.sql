-- ============================================================
-- V2__payroll_refactor.sql
-- Step 2 of 2: create the new normalized payroll schema.
--
-- Run AFTER V1 and AFTER deploying the new application code.
-- Hibernate ddl-auto=update will also create these tables on
-- startup — this script is provided as an explicit audit trail
-- and for environments that disable auto-DDL in production.
-- ============================================================

-- ── salary_structures ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS salary_structures (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    employee_id            BIGINT        NOT NULL,
    basic_salary           DECIMAL(15,2) NOT NULL,
    hra                    DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    conveyance_allowance   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    medical_allowance      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    special_allowance      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    other_allowances       DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    pf_employee            DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    pf_employer            DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    professional_tax       DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    esic_employee          DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    effective_from         DATE          NOT NULL,
    effective_to           DATE          NULL,
    currency               CHAR(3)       NOT NULL DEFAULT 'INR',
    created_at             DATETIME(6)   NULL,
    updated_at             DATETIME(6)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ss_employee FOREIGN KEY (employee_id) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── payroll_records ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS payroll_records (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    employee_id            BIGINT        NOT NULL,
    month                  INT           NOT NULL,
    year                   INT           NOT NULL,
    period_start           DATE          NOT NULL,
    period_end             DATE          NOT NULL,
    payment_date           DATE          NOT NULL,
    currency               CHAR(3)       NOT NULL DEFAULT 'INR',
    gross_earnings         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_deductions       DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    net_pay                DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    working_days_in_month  INT           NULL,
    days_worked            INT           NULL,
    days_on_leave          INT           NOT NULL DEFAULT 0,
    loss_of_pay_days       INT           NOT NULL DEFAULT 0,
    status                 VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    remarks                TEXT          NULL,
    processed_by           BIGINT        NULL,
    created_at             DATETIME(6)   NULL,
    updated_at             DATETIME(6)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_payroll_employee_month_year
        UNIQUE (employee_id, month, year),
    CONSTRAINT fk_pr_employee
        FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_pr_processed_by
        FOREIGN KEY (processed_by) REFERENCES employees (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── payroll_items ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payroll_items (
    id                  BIGINT         NOT NULL AUTO_INCREMENT,
    payroll_record_id   BIGINT         NOT NULL,
    item_code           VARCHAR(50)    NOT NULL,
    item_name           VARCHAR(120)   NOT NULL,
    item_category       VARCHAR(20)    NOT NULL,   -- EARNING | DEDUCTION | TAX
    amount              DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
    calculation_basis   VARCHAR(255)   NULL,
    is_system_generated TINYINT(1)     NOT NULL DEFAULT 1,
    display_order       INT            NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_pi_record
        FOREIGN KEY (payroll_record_id) REFERENCES payroll_records (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Useful indexes
CREATE INDEX idx_pr_employee_period  ON payroll_records (employee_id, year, month);
CREATE INDEX idx_pr_status           ON payroll_records (status);
CREATE INDEX idx_pr_payment_date     ON payroll_records (payment_date);
CREATE INDEX idx_pi_record_category  ON payroll_items   (payroll_record_id, item_category);
CREATE INDEX idx_ss_employee_period  ON salary_structures (employee_id, effective_from, effective_to);

-- ============================================================
-- DATA MIGRATION (optional — run when you are ready to import
-- old payroll_runs_backup rows that had structured payslipData)
-- ============================================================
-- If payslip_data was never populated in the old table, you
-- can skip this block.  If it was, write a custom INSERT
-- SELECT per your JSON structure.  Example stub:
--
-- INSERT INTO payroll_records
--     (employee_id, month, year, period_start, period_end,
--      payment_date, currency, status, processed_by, created_at, updated_at)
-- SELECT
--     b.employee_id,
--     b.month,
--     b.year,
--     DATE(CONCAT(b.year, '-', LPAD(b.month, 2, '0'), '-01')), -- period_start
--     LAST_DAY(CONCAT(b.year, '-', LPAD(b.month, 2, '0'), '-01')), -- period_end
--     b.run_date,
--     'INR',
--     b.status,
--     b.processed_by,
--     b.created_at,
--     b.updated_at
-- FROM payroll_runs_backup b
-- WHERE b.migrated_record_id IS NULL;
--
-- Then update migrated_record_id and insert payroll_items manually
-- from your parsed JSON.
-- ============================================================
