package com.orgpluse.payroll.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Input for the bulk payroll run.
 *
 * The engine fetches each employee's active SalaryStructure and builds
 * the PayrollItem list automatically.  HR only needs to supply the period
 * dates, working days, and optional processor.
 */
@Data
public class BulkPayrollRequest {

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    private Integer year;

    @NotNull(message = "Period start date is required")
    private LocalDate periodStart;

    @NotNull(message = "Period end date is required")
    private LocalDate periodEnd;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    private String currency = "INR";

    @NotNull(message = "Working days in month is required")
    @Min(value = 1)
    private Integer workingDaysInMonth;

    /** Optional HR employee who triggered this run */
    private Long processedById;

    private String remarks;

}
