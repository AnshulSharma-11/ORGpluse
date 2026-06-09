package com.orgpluse.payroll.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Allows updating header fields and/or replacing the full item list.
 * If items is null, the existing items are preserved.
 * If items is an empty list, all existing items are deleted.
 */
@Data
public class UpdatePayrollRequest {

    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @Min(value = 2000, message = "Year must be 2000 or later")
    private Integer year;

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate paymentDate;

    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    private String currency;

    private Integer workingDaysInMonth;
    private Integer daysWorked;
    private Integer daysOnLeave;
    private Integer lossOfPayDays;

    private String status;
    private Long processedById;
    private String remarks;

    /** When provided, fully replaces the current item list */
    @Valid
    private List<PayrollItemRequest> items;

}
