package com.orgpluse.payroll.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryStructureRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.01", message = "Basic salary must be greater than zero")
    private BigDecimal basicSalary;

    private BigDecimal hra              = BigDecimal.ZERO;
    private BigDecimal conveyanceAllowance = BigDecimal.ZERO;
    private BigDecimal medicalAllowance = BigDecimal.ZERO;
    private BigDecimal specialAllowance = BigDecimal.ZERO;
    private BigDecimal otherAllowances  = BigDecimal.ZERO;

    private BigDecimal pfEmployee       = BigDecimal.ZERO;
    private BigDecimal pfEmployer       = BigDecimal.ZERO;
    private BigDecimal professionalTax  = BigDecimal.ZERO;
    private BigDecimal esicEmployee     = BigDecimal.ZERO;

    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    /** Leave null for open-ended (currently active) structure */
    private LocalDate effectiveTo;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3)
    private String currency = "INR";

}
