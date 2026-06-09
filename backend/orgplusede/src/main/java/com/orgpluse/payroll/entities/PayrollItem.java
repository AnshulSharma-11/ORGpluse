package com.orgpluse.payroll.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * PayrollItem — one row per salary component inside a PayrollRecord.
 *
 * Examples:
 *   itemCode=BASIC,   itemName="Basic Salary",         category=EARNING,   amount=30000.00
 *   itemCode=HRA,     itemName="House Rent Allowance",  category=EARNING,   amount=12000.00
 *   itemCode=PF_EMP,  itemName="PF (Employee)",         category=DEDUCTION, amount=3600.00
 *   itemCode=TDS,     itemName="Tax Deducted at Source", category=TAX,      amount=2500.00
 *
 * Design notes:
 * - amount is always stored as a POSITIVE number for both earnings and deductions.
 *   The sign semantics are encoded in itemCategory, not in the number.
 * - calculationBasis lets auditors understand HOW the figure was derived
 *   (e.g. "30% of Basic", "Fixed") without storing opaque JSON.
 * - isSystemGenerated distinguishes items computed by the payroll engine from
 *   manual adjustments added by HR.
 */
@Entity
@Data
@Table(name = "payroll_items")
public class PayrollItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // ── Classification ────────────────────────────────────────────────────────

    /**
     * Short machine-readable code — used as a stable key for reporting.
     * Examples: BASIC, HRA, CONVEYANCE, MEDICAL, PF_EMP, PF_EMP_ER,
     *           PROF_TAX, TDS, LOAN_EMI, ADVANCE_RECOVERY
     */
    @NotBlank(message = "Item code is required")
    @Column(name = "item_code", nullable = false, length = 50)
    private String itemCode;

    /** Human-readable label shown on the payslip */
    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false, length = 120)
    private String itemName;

    @NotNull(message = "Item category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "item_category", nullable = false, length = 20)
    private PayrollItemCategory itemCategory;

    // ── Amount ────────────────────────────────────────────────────────────────

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be zero or positive")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    // ── Calculation metadata (audit trail) ────────────────────────────────────

    /**
     * Human-readable explanation of how the amount was derived.
     * Examples: "Fixed", "40% of Basic", "Actual reimbursement", "LOP adjustment"
     */
    @Column(name = "calculation_basis", length = 255)
    private String calculationBasis;

    /**
     * True = computed by the bulk payroll engine.
     * False = manually overridden or added by HR.
     */
    @Column(name = "is_system_generated", nullable = false)
    private boolean systemGenerated = true;

    /** Sequence for stable ordering on printed payslips */
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // ── Parent relationship ───────────────────────────────────────────────────

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_record_id", nullable = false)
    @JsonIgnoreProperties("items")
    private PayrollRecord payrollRecord;

}
