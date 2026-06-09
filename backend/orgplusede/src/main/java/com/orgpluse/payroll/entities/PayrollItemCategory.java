package com.orgpluse.payroll.entities;

/**
 * Three-way classification used on every PayrollItem row.
 *
 * EARNING    — adds to gross pay  (basic salary, HRA, overtime …)
 * DEDUCTION  — subtracts from gross pay  (PF employee contribution, loan EMI …)
 * TAX        — tax deductions at source (TDS, professional tax …)
 *
 * Stored as a VARCHAR column so MySQL records stay human-readable without
 * needing a lookup table.
 */
public enum PayrollItemCategory {
    EARNING,
    DEDUCTION,
    TAX
}
