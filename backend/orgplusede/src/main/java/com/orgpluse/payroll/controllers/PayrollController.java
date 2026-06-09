package com.orgpluse.payroll.controllers;

import com.orgpluse.payroll.dto.*;
import com.orgpluse.payroll.services.PayrollRecordService;
import com.orgpluse.response_wrapper.ResponseWrapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/payroll-records")
@CrossOrigin("*")
public class PayrollController {

    @Autowired
    private PayrollRecordService payrollRecordService;

    // ── CREATE ────────────────────────────────────────────────────────────────

    // POST /api/v1/admin/payroll-records
    @PostMapping
    public ResponseEntity<ResponseWrapper> create(
            @Valid @RequestBody CreatePayrollRequest request) {
        return payrollRecordService.create(request);
    }

    // POST /api/v1/admin/payroll-records/bulk
    @PostMapping("/bulk")
    public ResponseEntity<ResponseWrapper> bulkCreate(
            @Valid @RequestBody BulkPayrollRequest request) {
        return payrollRecordService.bulkCreate(request);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    // GET /api/v1/admin/payroll-records/{id}  — full record with items
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getById(@PathVariable Long id) {
        return payrollRecordService.getById(id);
    }

    // GET /api/v1/admin/payroll-records  — summary list
    @GetMapping
    public ResponseEntity<ResponseWrapper> getAll(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return payrollRecordService.getAll(sortBy, sortDirection, page, size);
    }

    // GET /api/v1/admin/payroll-records/employee/{employeeId}
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseWrapper> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return payrollRecordService.getByEmployee(employeeId, page, size);
    }

    // GET /api/v1/admin/payroll-records/filter
    @GetMapping("/filter")
    public ResponseEntity<ResponseWrapper> filter(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long processedBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return payrollRecordService.filter(employeeId, month, year, status,
                processedBy, sortBy, sortDirection, page, size);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    // PUT /api/v1/admin/payroll-records/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePayrollRequest request) {
        return payrollRecordService.update(id, request);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    // DELETE /api/v1/admin/payroll-records/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable Long id) {
        return payrollRecordService.delete(id);
    }

}
