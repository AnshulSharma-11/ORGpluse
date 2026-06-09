package com.orgpluse.payroll.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orgpluse.entities.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * SalaryStructure stores the contractual salary breakdown for a specific employee.
 *
 * When the bulk payroll engine runs, it looks up the employee's active
 * SalaryStructure and uses it to seed the PayrollItem list automatically.
 *
 * effectiveFrom / effectiveTo enables salary revision history — the engine
 * always picks the record whose effectiveFrom <= payroll month AND
 * (effectiveTo IS NULL OR effectiveTo >= payroll month).
 *
 * All component amounts are monthly figures in the base currency.
 * Optional components are zero when not applicable.
 */
@Entity
@Data
@Table(name = "salary_structures")
@EntityListeners(AuditingEntityListener.class)
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"department", "designation", "branch",
            "manager", "createdAt", "updatedAt", "passwordHash"})
    private Employee employee;

    // ── Earnings ──────────────────────────────────────────────────────────────

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.01", message = "Basic salary must be greater than zero")
    @Column(name = "basic_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "hra", precision = 15, scale = 2)
    private BigDecimal hra = BigDecimal.ZERO;

    @Column(name = "conveyance_allowance", precision = 15, scale = 2)
    private BigDecimal conveyanceAllowance = BigDecimal.ZERO;

    @Column(name = "medical_allowance", precision = 15, scale = 2)
    private BigDecimal medicalAllowance = BigDecimal.ZERO;

    @Column(name = "special_allowance", precision = 15, scale = 2)
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    @Column(name = "other_allowances", precision = 15, scale = 2)
    private BigDecimal otherAllowances = BigDecimal.ZERO;

    // ── Deductions ────────────────────────────────────────────────────────────

    /** Employee PF contribution (typically 12% of basic) */
    @Column(name = "pf_employee", precision = 15, scale = 2)
    private BigDecimal pfEmployee = BigDecimal.ZERO;

    /** Employer PF contribution (informational — shown on CTC, not deducted) */
    @Column(name = "pf_employer", precision = 15, scale = 2)
    private BigDecimal pfEmployer = BigDecimal.ZERO;

    @Column(name = "professional_tax", precision = 15, scale = 2)
    private BigDecimal professionalTax = BigDecimal.ZERO;

    @Column(name = "esic_employee", precision = 15, scale = 2)
    private BigDecimal esicEmployee = BigDecimal.ZERO;

    // ── Effective period ──────────────────────────────────────────────────────

    @NotNull(message = "Effective from date is required")
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** Null means currently active */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @NotBlank
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "INR";

    // ── Audit ─────────────────────────────────────────────────────────────────

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

}
