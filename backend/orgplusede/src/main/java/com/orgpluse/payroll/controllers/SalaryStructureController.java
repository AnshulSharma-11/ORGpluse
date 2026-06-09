package com.orgpluse.payroll.controllers;

import com.orgpluse.payroll.dto.SalaryStructureRequest;
import com.orgpluse.payroll.services.SalaryStructureService;
import com.orgpluse.response_wrapper.ResponseWrapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/salary-structures")
@CrossOrigin("*")
public class SalaryStructureController {

    @Autowired
    private SalaryStructureService salaryStructureService;

    // POST /api/v1/admin/salary-structures
    @PostMapping
    public ResponseEntity<ResponseWrapper> create(
            @Valid @RequestBody SalaryStructureRequest request) {
        return salaryStructureService.create(request);
    }

    // GET /api/v1/admin/salary-structures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getById(@PathVariable Long id) {
        return salaryStructureService.getById(id);
    }

    // GET /api/v1/admin/salary-structures/employee/{employeeId}
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ResponseWrapper> getByEmployee(@PathVariable Long employeeId) {
        return salaryStructureService.getByEmployee(employeeId);
    }

    // PUT /api/v1/admin/salary-structures/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper> update(
            @PathVariable Long id,
            @Valid @RequestBody SalaryStructureRequest request) {
        return salaryStructureService.update(id, request);
    }

    // DELETE /api/v1/admin/salary-structures/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> delete(@PathVariable Long id) {
        return salaryStructureService.delete(id);
    }

}
