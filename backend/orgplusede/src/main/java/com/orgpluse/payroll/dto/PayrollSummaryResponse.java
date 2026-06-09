package com.orgpluse.payroll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Returned by list/filter endpoints.  Omits the full items list for
 * performance — use GET /payroll/{id} to fetch the full payslip with items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollSummaryResponse {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private Integer month;
    private Integer year;
    private LocalDate paymentDate;
    private String currency;
    private BigDecimal grossEarnings;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private Integer daysWorked;
    private Integer lossOfPayDays;
    private String status;

}
