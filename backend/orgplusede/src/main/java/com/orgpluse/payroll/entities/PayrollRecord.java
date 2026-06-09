package com.orgpluse.payroll.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orgpluse.entities.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PayrollRecord is the header (one row per employee per month).
 *
 * Replaces the old PayrollRun entity.  The old table (payroll_runs) is preserved
 * untouched during the migration window — see V2__payroll_refactor.sql.
 *
 * Computed totals (grossEarnings, totalDeductions, netPay) are stored as
 * DECIMAL columns so they can be queried, sorted, and reported on without
 * re-aggregating child rows every time.  They are recalculated in the service
 * layer whenever items change.
 */
@Entity
@Data
@Table(
    name = "payroll_records",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_payroll_employee_month_year",
        columnNames = {"employee_id", "month", "year"}
    )
)
@EntityListeners(AuditingEntityListener.class)
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // ── Period ────────────────────────────────────────────────────────────────

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(nullable = false)
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Pay period start date is required")
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull(message = "Pay period end date is required")
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    // ── Currency ──────────────────────────────────────────────────────────────

    /** ISO-4217 currency code, e.g. INR, USD */
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Column(nullable = false, length = 3)
    private String currency = "INR";

    // ── Computed summary totals (denormalised for query performance) ──────────

    @Column(name = "gross_earnings", precision = 15, scale = 2)
    private BigDecimal grossEarnings = BigDecimal.ZERO;

    @Column(name = "total_deductions", precision = 15, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    @Column(name = "net_pay", precision = 15, scale = 2)
    private BigDecimal netPay = BigDecimal.ZERO;

    // ── Working days ──────────────────────────────────────────────────────────

    @Column(name = "working_days_in_month")
    private Integer workingDaysInMonth;

    @Column(name = "days_worked")
    private Integer daysWorked;

    @Column(name = "days_on_leave")
    private Integer daysOnLeave = 0;

    @Column(name = "loss_of_pay_days")
    private Integer lossOfPayDays = 0;

    // ── Status — DRAFT / PROCESSED / APPROVED / PAID ─────────────────────────

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status = "DRAFT";

    // ── Notes / remarks ───────────────────────────────────────────────────────

    @Column(columnDefinition = "TEXT")
    private String remarks;

    // ── Relationships ─────────────────────────────────────────────────────────

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"department", "designation", "branch",
            "manager", "createdAt", "updatedAt", "passwordHash"})
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    @JsonIgnoreProperties({"department", "designation", "branch",
            "manager", "createdAt", "updatedAt", "passwordHash"})
    private Employee processedBy;

    /**
     * Child line-items.  CascadeType.ALL + orphanRemoval means the service
     * can simply rebuild the items list and save the parent — child inserts,
     * updates, and deletes happen automatically.
     */
    @OneToMany(mappedBy = "payrollRecord",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    @JsonIgnoreProperties("payrollRecord")
    private List<PayrollItem> items = new ArrayList<>();

    // ── Audit ─────────────────────────────────────────────────────────────────

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // ── Helper: rebuild computed totals from child items ──────────────────────

    public void recalculateTotals() {
        this.grossEarnings = items.stream()
                .filter(i -> i.getItemCategory() == PayrollItemCategory.EARNING)
                .map(PayrollItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalDeductions = items.stream()
                .filter(i -> i.getItemCategory() == PayrollItemCategory.DEDUCTION
                          || i.getItemCategory() == PayrollItemCategory.TAX)
                .map(PayrollItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.netPay = this.grossEarnings.subtract(this.totalDeductions);
    }

}
