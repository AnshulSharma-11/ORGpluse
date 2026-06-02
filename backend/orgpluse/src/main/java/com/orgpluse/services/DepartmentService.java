package com.orgpluse.services;

import com.orgpluse.entities.Department;
import com.orgpluse.entities.Employee;
import com.orgpluse.repositories.DepartmentRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.DepartmentSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────
    // Save order to handle circular dependency:
    //   Step 1 → Save department with manager = null
    //   Step 2 → If managerId provided, validate employee exists
    //   Step 3 → Set manager and save again

    public ResponseEntity<ResponseWrapper> addDepartment(Department department) {
        Employee manager = department.getManager();
        department.setManager(null);

        // Validate parent department if provided
        if (department.getParentDepartment() != null
                && department.getParentDepartment().getId() != null) {
            Long parentId = department.getParentDepartment().getId();
            Optional<Department> parentDept = departmentRepository.findById(parentId);
            if (parentDept.isEmpty()) {
                return response.send("Parent department not found with id: " + parentId,
                        null, HttpStatus.NOT_FOUND);
            }
            department.setParentDepartment(parentDept.get());
        }

        // Step 1: Save without manager to avoid circular constraint
        Department saved = departmentRepository.save(department);

        // Step 2 + 3: Attach manager after department is persisted
        if (manager != null && manager.getId() != null) {
            Optional<Employee> emp = employeeRepository.findById(manager.getId());
            if (emp.isEmpty()) {
                return response.send("Manager (Employee) not found with id: " + manager.getId(),
                        null, HttpStatus.NOT_FOUND);
            }
            saved.setManager(emp.get());
            saved = departmentRepository.save(saved);
        }

        return response.send("Department created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getDepartmentById(Long id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isEmpty()) {
            return response.send("Department not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        return response.send("Department fetched successfully", department.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllDepartments(String search,
                                                              String sortBy,
                                                              String sortDirection) {
        Specification<Department> spec = Specification
                .where(DepartmentSpecification.searchByName(search))
                .and(DepartmentSpecification.sortByField(sortBy, sortDirection));

        List<Department> departments = departmentRepository.findAll(spec);
        return response.send("Departments fetched successfully", departments, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateDepartment(Long id, Department updatedDepartment) {
        Optional<Department> existing = departmentRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Department not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Department department = existing.get();
        department.setName(updatedDepartment.getName());
        department.setDescription(updatedDepartment.getDescription());

        if (updatedDepartment.getIsActive() != null) {
            department.setIsActive(updatedDepartment.getIsActive());
        }

        // Update manager if provided
        if (updatedDepartment.getManager() != null
                && updatedDepartment.getManager().getId() != null) {
            Long managerId = updatedDepartment.getManager().getId();
            Optional<Employee> manager = employeeRepository.findById(managerId);
            if (manager.isEmpty()) {
                return response.send("Manager (Employee) not found with id: " + managerId,
                        null, HttpStatus.NOT_FOUND);
            }
            department.setManager(manager.get());
        } else {
            department.setManager(null);
        }

        // Update parent department if provided
        if (updatedDepartment.getParentDepartment() != null
                && updatedDepartment.getParentDepartment().getId() != null) {
            Long parentId = updatedDepartment.getParentDepartment().getId();
            if (parentId.equals(id)) {
                return response.send("A department cannot be its own parent",
                        null, HttpStatus.BAD_REQUEST);
            }
            Optional<Department> parentDept = departmentRepository.findById(parentId);
            if (parentDept.isEmpty()) {
                return response.send("Parent department not found with id: " + parentId,
                        null, HttpStatus.NOT_FOUND);
            }
            department.setParentDepartment(parentDept.get());
        } else {
            department.setParentDepartment(null);
        }

        Department saved = departmentRepository.save(department);
        return response.send("Department updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE (SOFT) ─────────────────────────────────────────────────────────
    // Sets isActive = false instead of removing the record.
    // Preserves referential integrity for Employee FKs pointing to this department.

    public ResponseEntity<ResponseWrapper> deleteDepartment(Long id) {
        Optional<Department> existing = departmentRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Department not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Department department = existing.get();
        department.setIsActive(false);
        departmentRepository.save(department);

        return response.send("Department deactivated successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterDepartments(Boolean isActive,
                                                              Long managerId,
                                                              Long parentDepartmentId,
                                                              String sortBy,
                                                              String sortDirection) {
        Specification<Department> spec = Specification
                .where(DepartmentSpecification.hasIsActive(isActive))
                .and(DepartmentSpecification.hasManager(managerId))
                .and(DepartmentSpecification.hasParentDepartment(parentDepartmentId))
                .and(DepartmentSpecification.sortByField(sortBy, sortDirection));

        List<Department> departments = departmentRepository.findAll(spec);
        return response.send("Departments filtered successfully", departments, HttpStatus.OK);
    }

}
