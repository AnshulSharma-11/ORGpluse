package com.orgpluse.payroll.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.payroll.dto.*;
import com.orgpluse.payroll.entities.*;
import com.orgpluse.payroll.repositories.*;
import com.orgpluse.payroll.specifications.PayrollRecordSpecification;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;

import com.orgpluse.common.PageResponse;
import com.orgpluse.common.PageableUtils;
import com.orgpluse.exception.BadRequestException;
import com.orgpluse.exception.DuplicateResourceException;
import com.orgpluse.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayrollRecordService {

    private static final java.util.Set<String> ALLOWED_SORTS = java.util.Set.of(
            "id", "year", "month", "paymentDate", "netPay",
            "grossEarnings", "totalDeductions", "status", "createdAt");

    @Autowired private PayrollRecordRepository payrollRecordRepository;
    @Autowired private PayrollItemRepository payrollItemRepository;
    @Autowired private SalaryStructureRepository salaryStructureRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<ResponseWrapper> create(CreatePayrollRequest req) {

        // Validate employee
        Optional<Employee> empOpt = employeeRepository.findById(req.getEmployeeId());
        Employee emp = employeeRepository.findById(req.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));

        // Duplicate check
        if (payrollRecordRepository.existsByEmployeeIdAndMonthAndYear(
                req.getEmployeeId(), req.getMonth(), req.getYear()))
            throw new DuplicateResourceException(
                    "A payroll record already exists for this employee for "
                    + req.getMonth() + "/" + req.getYear());

        // Resolve optional processedBy
        Employee processor = resolveProcessor(req.getProcessedById());
        if (processor == null && req.getProcessedById() != null)
            throw new ResourceNotFoundException("Processor", req.getProcessedById());

        // Build header
        PayrollRecord record = new PayrollRecord();
        record.setEmployee(emp);
        record.setMonth(req.getMonth());
        record.setYear(req.getYear());
        record.setPeriodStart(req.getPeriodStart());
        record.setPeriodEnd(req.getPeriodEnd());
        record.setPaymentDate(req.getPaymentDate());
        record.setCurrency(req.getCurrency());
        record.setWorkingDaysInMonth(req.getWorkingDaysInMonth());
        record.setDaysWorked(req.getDaysWorked());
        record.setDaysOnLeave(req.getDaysOnLeave() != null ? req.getDaysOnLeave() : 0);
        record.setLossOfPayDays(req.getLossOfPayDays() != null ? req.getLossOfPayDays() : 0);
        record.setStatus("DRAFT");
        record.setProcessedBy(processor);
        record.setRemarks(req.getRemarks());

        // Build items
        List<PayrollItem> items = buildItems(req.getItems(), record);
        record.setItems(items);
        record.recalculateTotals();

        PayrollRecord saved = payrollRecordRepository.save(record);
        return response.send("Payroll record created successfully", saved, HttpStatus.CREATED);
    }

    // ── BULK CREATE ───────────────────────────────────────────────────────────

    /**
     * Runs payroll for all ACTIVE employees for the given month/year.
     * Each employee's active SalaryStructure is resolved to seed the items.
     * Employees without a SalaryStructure are skipped with a warning.
     * Employees already processed for the period are skipped silently.
     */
    @Transactional
    public ResponseEntity<ResponseWrapper> bulkCreate(BulkPayrollRequest req) {

        // Resolve optional processor
        Employee processor = resolveProcessor(req.getProcessedById());
        if (processor == null && req.getProcessedById() != null)
            throw new ResourceNotFoundException("Processor", req.getProcessedById());

        // All active employees — DB-level WHERE status = 'ACTIVE'.
        // Replaces findAll().stream().filter(...) so only active rows
        // are transferred over JDBC; inactive employees never reach the JVM.
        List<Employee> active = employeeRepository.findByStatusIgnoreCase("ACTIVE");

        if (active.isEmpty())
            throw new BadRequestException("No active employees found for bulk payroll run");

        // Pre-load already-processed IDs for this period (avoids N+1 queries)
        Set<Long> alreadyProcessed = new HashSet<>(
                payrollRecordRepository.findEmployeeIdsByMonthAndYear(req.getMonth(), req.getYear()));

        // Reference date for salary structure lookup = first day of the payroll month
        LocalDate referenceDate = LocalDate.of(req.getYear(), req.getMonth(), 1);

        List<PayrollRecord> created = new ArrayList<>();
        List<String> skippedDuplicate = new ArrayList<>();
        List<String> skippedNoStructure = new ArrayList<>();

        for (Employee emp : active) {

            // Skip duplicate
            if (alreadyProcessed.contains(emp.getId())) {
                skippedDuplicate.add(emp.getFirstName() + " " + emp.getLastName());
                continue;
            }

            // Resolve salary structure
            Optional<SalaryStructure> ssOpt = salaryStructureRepository
                    .findActiveStructure(emp.getId(), referenceDate);
            if (ssOpt.isEmpty()) {
                skippedNoStructure.add(emp.getFirstName() + " " + emp.getLastName()
                        + " (no salary structure)");
                continue;
            }

            SalaryStructure ss = ssOpt.get();

            // Compute LOP-adjusted amounts
            int workingDays = req.getWorkingDaysInMonth();
            // daysWorked defaults to full month when not specified per-employee
            int daysWorked = workingDays;
            int lopDays = 0;
            BigDecimal lopFactor = BigDecimal.ONE;
            if (lopDays > 0) {
                lopFactor = BigDecimal.valueOf((double)(workingDays - lopDays) / workingDays);
            }

            // Build header
            PayrollRecord record = new PayrollRecord();
            record.setEmployee(emp);
            record.setMonth(req.getMonth());
            record.setYear(req.getYear());
            record.setPeriodStart(req.getPeriodStart());
            record.setPeriodEnd(req.getPeriodEnd());
            record.setPaymentDate(req.getPaymentDate());
            record.setCurrency(req.getCurrency());
            record.setWorkingDaysInMonth(workingDays);
            record.setDaysWorked(daysWorked);
            record.setDaysOnLeave(0);
            record.setLossOfPayDays(lopDays);
            record.setStatus("PROCESSED");
            record.setProcessedBy(processor);
            record.setRemarks(req.getRemarks());

            // Build items from salary structure
            List<PayrollItem> items = buildItemsFromStructure(ss, record, lopFactor);
            record.setItems(items);
            record.recalculateTotals();

            created.add(payrollRecordRepository.save(record));
        }

        // Build result message
        StringBuilder msg = new StringBuilder(created.size() + " payslip(s) created");
        if (!skippedDuplicate.isEmpty()) {
            msg.append("; already processed: ").append(String.join(", ", skippedDuplicate));
        }
        if (!skippedNoStructure.isEmpty()) {
            msg.append("; skipped (no structure): ").append(String.join(", ", skippedNoStructure));
        }

        return response.send(msg.toString(), created, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getById(Long id) {
        PayrollRecord rec = payrollRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record", id));
        return response.send("Payroll record fetched successfully", rec, HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAll(String sortBy, String sortDirection,
                                                    Integer page, Integer size) {
        Pageable pageable = PageableUtils.of(page, size, sortBy, sortDirection, ALLOWED_SORTS);
        Page<PayrollRecord> result = payrollRecordRepository.findAll(pageable);
        return response.send("Payroll records fetched successfully",
                new PageResponse<>(result.map(this::toSummary)), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getByEmployee(Long employeeId,
                                                          Integer page, Integer size) {
        Pageable pageable = PageableUtils.of(page, size, "year", "desc");
        Page<PayrollRecord> result =
                payrollRecordRepository.findByEmployeeId(employeeId, pageable);
        return response.send("Payroll records fetched successfully",
                new PageResponse<>(result.map(this::toSummary)), HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<ResponseWrapper> update(Long id, UpdatePayrollRequest req) {
        PayrollRecord record = payrollRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record", id));

        // Guard: PAID records may not be modified
        if ("PAID".equalsIgnoreCase(record.getStatus()))
            throw new BadRequestException("PAID payroll records cannot be modified");

        // Apply scalar updates (null = no change)
        if (req.getMonth() != null)              record.setMonth(req.getMonth());
        if (req.getYear() != null)               record.setYear(req.getYear());
        if (req.getPeriodStart() != null)        record.setPeriodStart(req.getPeriodStart());
        if (req.getPeriodEnd() != null)          record.setPeriodEnd(req.getPeriodEnd());
        if (req.getPaymentDate() != null)        record.setPaymentDate(req.getPaymentDate());
        if (req.getCurrency() != null)           record.setCurrency(req.getCurrency());
        if (req.getWorkingDaysInMonth() != null) record.setWorkingDaysInMonth(req.getWorkingDaysInMonth());
        if (req.getDaysWorked() != null)         record.setDaysWorked(req.getDaysWorked());
        if (req.getDaysOnLeave() != null)        record.setDaysOnLeave(req.getDaysOnLeave());
        if (req.getLossOfPayDays() != null)      record.setLossOfPayDays(req.getLossOfPayDays());
        if (req.getStatus() != null)             record.setStatus(req.getStatus());
        if (req.getRemarks() != null)            record.setRemarks(req.getRemarks());

        if (req.getProcessedById() != null) {
            Employee processor = resolveProcessor(req.getProcessedById());
            if (processor == null)
                throw new ResourceNotFoundException("Processor", req.getProcessedById());
            record.setProcessedBy(processor);
        }

        // Replace items only when the caller supplies a new list
        if (req.getItems() != null) {
            record.getItems().clear();
            List<PayrollItem> newItems = buildItems(req.getItems(), record);
            record.getItems().addAll(newItems);
            record.recalculateTotals();
        }

        PayrollRecord saved = payrollRecordRepository.save(record);
        return response.send("Payroll record updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public ResponseEntity<ResponseWrapper> delete(Long id) {
        PayrollRecord rec = payrollRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record", id));
        if ("PAID".equalsIgnoreCase(rec.getStatus()))
            throw new BadRequestException("PAID payroll records cannot be deleted");
        payrollRecordRepository.deleteById(id);
        return response.send("Payroll record deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filter(Long employeeId, Integer month, Integer year,
                                                   String status, Long processedById,
                                                   String sortBy, String sortDirection,
                                                   Integer page, Integer size) {
        Pageable pageable = PageableUtils.of(page, size, sortBy, sortDirection, ALLOWED_SORTS);
        Specification<PayrollRecord> spec = Specification
                .where(PayrollRecordSpecification.hasEmployee(employeeId))
                .and(PayrollRecordSpecification.hasMonth(month))
                .and(PayrollRecordSpecification.hasYear(year))
                .and(PayrollRecordSpecification.hasStatus(status))
                .and(PayrollRecordSpecification.hasProcessedBy(processedById));

        Page<PayrollRecord> result = payrollRecordRepository.findAll(spec, pageable);
        return response.send("Payroll records filtered successfully",
                new PageResponse<>(result.map(this::toSummary)), HttpStatus.OK);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────

    private Employee resolveProcessor(Long processedById) {
        if (processedById == null) return null;
        return employeeRepository.findById(processedById).orElse(null);
    }

    /** Converts a list of PayrollItemRequest DTOs into PayrollItem entities */
    private List<PayrollItem> buildItems(List<PayrollItemRequest> dtos, PayrollRecord record) {
        List<PayrollItem> items = new ArrayList<>();
        for (PayrollItemRequest dto : dtos) {
            PayrollItem item = new PayrollItem();
            item.setItemCode(dto.getItemCode());
            item.setItemName(dto.getItemName());
            item.setItemCategory(dto.getItemCategory());
            item.setAmount(dto.getAmount() != null ? dto.getAmount() : BigDecimal.ZERO);
            item.setCalculationBasis(dto.getCalculationBasis());
            item.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
            item.setSystemGenerated(false);  // manually provided by caller
            item.setPayrollRecord(record);
            items.add(item);
        }
        return items;
    }

    /** Builds structured PayrollItems from a SalaryStructure, applying LOP factor */
    private List<PayrollItem> buildItemsFromStructure(SalaryStructure ss,
                                                       PayrollRecord record,
                                                       BigDecimal lopFactor) {
        List<PayrollItem> items = new ArrayList<>();
        int order = 10;

        // Earnings
        items.add(earning(record, "BASIC", "Basic Salary",
                ss.getBasicSalary().multiply(lopFactor), "LOP-adjusted basic", order += 10));
        items.add(earning(record, "HRA", "House Rent Allowance",
                ss.getHra().multiply(lopFactor), "40% of Basic (LOP-adjusted)", order += 10));
        items.add(earning(record, "CONVEYANCE", "Conveyance Allowance",
                ss.getConveyanceAllowance(), "Fixed", order += 10));
        items.add(earning(record, "MEDICAL", "Medical Allowance",
                ss.getMedicalAllowance(), "Fixed", order += 10));
        items.add(earning(record, "SPECIAL", "Special Allowance",
                ss.getSpecialAllowance().multiply(lopFactor), "LOP-adjusted", order += 10));
        if (ss.getOtherAllowances().compareTo(BigDecimal.ZERO) > 0) {
            items.add(earning(record, "OTHER_ALLOW", "Other Allowances",
                    ss.getOtherAllowances(), "Fixed", order += 10));
        }

        // Deductions
        if (ss.getPfEmployee().compareTo(BigDecimal.ZERO) > 0) {
            items.add(deduction(record, "PF_EMP", "PF (Employee)",
                    ss.getPfEmployee(), "12% of Basic", order += 10));
        }
        if (ss.getProfessionalTax().compareTo(BigDecimal.ZERO) > 0) {
            items.add(deduction(record, "PROF_TAX", "Professional Tax",
                    ss.getProfessionalTax(), "State slab", order += 10));
        }
        if (ss.getEsicEmployee().compareTo(BigDecimal.ZERO) > 0) {
            items.add(deduction(record, "ESIC_EMP", "ESIC (Employee)",
                    ss.getEsicEmployee(), "0.75% of Gross", order += 10));
        }

        return items;
    }

    private PayrollItem earning(PayrollRecord record, String code, String name,
                                 BigDecimal amount, String basis, int order) {
        return item(record, code, name, PayrollItemCategory.EARNING, amount, basis, order);
    }

    private PayrollItem deduction(PayrollRecord record, String code, String name,
                                   BigDecimal amount, String basis, int order) {
        return item(record, code, name, PayrollItemCategory.DEDUCTION, amount, basis, order);
    }

    private PayrollItem item(PayrollRecord record, String code, String name,
                              PayrollItemCategory category, BigDecimal amount,
                              String basis, int order) {
        PayrollItem i = new PayrollItem();
        i.setItemCode(code);
        i.setItemName(name);
        i.setItemCategory(category);
        i.setAmount(amount.setScale(2, java.math.RoundingMode.HALF_UP));
        i.setCalculationBasis(basis);
        i.setSystemGenerated(true);
        i.setDisplayOrder(order);
        i.setPayrollRecord(record);
        return i;
    }

    // Sort is now handled by Pageable (PageableUtils.of); buildSortSpec removed.

    private PayrollSummaryResponse toSummary(PayrollRecord r) {
        return new PayrollSummaryResponse(
                r.getId(),
                r.getEmployee().getId(),
                r.getEmployee().getEmployeeCode(),
                r.getEmployee().getFirstName() + " " + r.getEmployee().getLastName(),
                r.getMonth(),
                r.getYear(),
                r.getPaymentDate(),
                r.getCurrency(),
                r.getGrossEarnings(),
                r.getTotalDeductions(),
                r.getNetPay(),
                r.getDaysWorked(),
                r.getLossOfPayDays(),
                r.getStatus());
    }

}
