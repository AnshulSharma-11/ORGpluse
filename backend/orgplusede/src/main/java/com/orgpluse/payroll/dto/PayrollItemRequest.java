package com.orgpluse.payroll.dto;

import com.orgpluse.payroll.entities.PayrollItemCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollItemRequest {

    @NotBlank(message = "Item code is required")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @NotNull(message = "Item category is required — EARNING, DEDUCTION, or TAX")
    private PayrollItemCategory itemCategory;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", message = "Amount must be zero or positive")
    private BigDecimal amount;

    private String calculationBasis;

    private Integer displayOrder = 0;

}
