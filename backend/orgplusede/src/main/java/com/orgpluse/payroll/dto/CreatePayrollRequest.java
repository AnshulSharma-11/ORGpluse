package com.orgpluse.payroll.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePayrollRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

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
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency = "INR";

    private Integer workingDaysInMonth;
    private Integer daysWorked;
    private Integer daysOnLeave = 0;
    private Integer lossOfPayDays = 0;

    /** Optional: HR employee who ran this payroll */
    private Long processedById;

    private String remarks;

    @Valid
    @NotEmpty(message = "At least one payroll item is required")
    private List<PayrollItemRequest> items = new ArrayList<>();

}
