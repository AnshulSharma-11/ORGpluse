package com.orgpluse.controllers;

import com.orgpluse.entities.Department;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.DepartmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // POST /api/v1/admin/departments
    @PostMapping("/departments")
    public ResponseEntity<ResponseWrapper> addDepartment(@RequestBody Department department) {
        return departmentService.addDepartment(department);
    }

    // PUT /api/v1/admin/departments/{id}
    @PutMapping("/departments/{id}")
    public ResponseEntity<ResponseWrapper> updateDepartment(@PathVariable Long id,
                                                             @RequestBody Department department) {
        return departmentService.updateDepartment(id, department);
    }

    // DELETE /api/v1/admin/departments/{id}  — soft delete
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ResponseWrapper> deleteDepartment(@PathVariable Long id) {
        return departmentService.deleteDepartment(id);
    }

    // GET /api/v1/admin/departments/{id}
    @GetMapping("/departments/{id}")
    public ResponseEntity<ResponseWrapper> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id);
    }

    // GET /api/v1/admin/departments?search=&sortBy=&sortDirection=
    @GetMapping("/departments")
    public ResponseEntity<ResponseWrapper> getAllDepartments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return departmentService.getAllDepartments(search, sortBy, sortDirection);
    }

    // GET /api/v1/admin/departments/filter?isActive=&managerId=&parentDepartmentId=&sortBy=&sortDirection=
    @GetMapping("/departments/filter")
    public ResponseEntity<ResponseWrapper> filterDepartments(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) Long parentDepartmentId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return departmentService.filterDepartments(isActive, managerId,
                parentDepartmentId, sortBy, sortDirection);
    }

}
