package com.orgpluse.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.entities.PayrollRun;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.repositories.PayrollRunRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.PayrollRunSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PayrollRunService {

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addPayrollRun(PayrollRun payrollRun) {
        // Validate processedBy employee (optional — payroll may be system-generated)
        if (payrollRun.getProcessedBy() != null
                && payrollRun.getProcessedBy().getId() != null) {
            Optional<Employee> processor = employeeRepository.findById(
                    payrollRun.getProcessedBy().getId());
            if (processor.isEmpty()) {
                return response.send("Processor (Employee) not found with id: "
                        + payrollRun.getProcessedBy().getId(), null, HttpStatus.NOT_FOUND);
            }
            payrollRun.setProcessedBy(processor.get());
        } else {
            payrollRun.setProcessedBy(null);
        }

        // Default status to DRAFT if not provided
        if (payrollRun.getStatus() == null || payrollRun.getStatus().isBlank()) {
            payrollRun.setStatus("DRAFT");
        }

        PayrollRun saved = payrollRunRepository.save(payrollRun);
        return response.send("Payroll run created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getPayrollRunById(Long id) {
        Optional<PayrollRun> payrollRun = payrollRunRepository.findById(id);
        if (payrollRun.isEmpty()) {
            return response.send("Payroll run not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Payroll run fetched successfully", payrollRun.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllPayrollRuns(String sortBy,
                                                              String sortDirection) {
        Specification<PayrollRun> spec = Specification
                .where(PayrollRunSpecification.sortByField(sortBy, sortDirection));

        List<PayrollRun> runs = payrollRunRepository.findAll(spec);
        return response.send("Payroll runs fetched successfully", runs, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updatePayrollRun(Long id,
                                                             PayrollRun updatedRun) {
        Optional<PayrollRun> existing = payrollRunRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Payroll run not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }

        PayrollRun run = existing.get();
        run.setMonth(updatedRun.getMonth());
        run.setYear(updatedRun.getYear());
        run.setRunDate(updatedRun.getRunDate());
        run.setStatus(updatedRun.getStatus());
        run.setPayslipData(updatedRun.getPayslipData());

        if (updatedRun.getProcessedBy() != null
                && updatedRun.getProcessedBy().getId() != null) {
            Optional<Employee> processor = employeeRepository.findById(
                    updatedRun.getProcessedBy().getId());
            if (processor.isEmpty()) {
                return response.send("Processor (Employee) not found with id: "
                        + updatedRun.getProcessedBy().getId(), null, HttpStatus.NOT_FOUND);
            }
            run.setProcessedBy(processor.get());
        } else {
            run.setProcessedBy(null);
        }

        PayrollRun saved = payrollRunRepository.save(run);
        return response.send("Payroll run updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deletePayrollRun(Long id) {
        Optional<PayrollRun> run = payrollRunRepository.findById(id);
        if (run.isEmpty()) {
            return response.send("Payroll run not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        payrollRunRepository.deleteById(id);
        return response.send("Payroll run deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterPayrollRuns(Integer month,
                                                              Integer year,
                                                              String status,
                                                              Long processedById,
                                                              String sortBy,
                                                              String sortDirection) {
        // Use compound year+month sort when no specific field is given
        Specification<PayrollRun> sortSpec = (sortBy != null && !sortBy.isBlank())
                ? PayrollRunSpecification.sortByField(sortBy, sortDirection)
                : PayrollRunSpecification.sortByYearMonth(sortDirection);

        Specification<PayrollRun> spec = Specification
                .where(PayrollRunSpecification.hasMonth(month))
                .and(PayrollRunSpecification.hasYear(year))
                .and(PayrollRunSpecification.hasStatus(status))
                .and(PayrollRunSpecification.hasProcessedBy(processedById))
                .and(sortSpec);

        List<PayrollRun> runs = payrollRunRepository.findAll(spec);
        return response.send("Payroll runs filtered successfully", runs, HttpStatus.OK);
    }

}
