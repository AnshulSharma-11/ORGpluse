package com.orgpluse.services;

import com.orgpluse.entities.Department;
import com.orgpluse.entities.Designation;
import com.orgpluse.entities.Employee;
import com.orgpluse.entities.EmployeeJobHistory;
import com.orgpluse.repositories.DepartmentRepository;
import com.orgpluse.repositories.DesignationRepository;
import com.orgpluse.repositories.EmployeeJobHistoryRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.EmployeeJobHistorySpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeJobHistoryService {

    @Autowired
    private EmployeeJobHistoryRepository jobHistoryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private UniversalResponse response;

    // ── Shared FK resolution helper ───────────────────────────────────────────

    private ResponseEntity<ResponseWrapper> resolveAndSetFKs(EmployeeJobHistory history) {

        // Employee (required)
        if (history.getEmployee() == null || history.getEmployee().getId() == null) {
            return response.send("Employee is required", null, HttpStatus.BAD_REQUEST);
        }
        Optional<Employee> emp = employeeRepository.findById(history.getEmployee().getId());
        if (emp.isEmpty()) {
            return response.send("Employee not found with id: "
                    + history.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
        }
        history.setEmployee(emp.get());

        // Old Department (optional)
        if (history.getOldDepartment() != null
                && history.getOldDepartment().getId() != null) {
            Optional<Department> oldDept = departmentRepository.findById(
                    history.getOldDepartment().getId());
            if (oldDept.isEmpty()) {
                return response.send("Old department not found with id: "
                        + history.getOldDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            history.setOldDepartment(oldDept.get());
        } else {
            history.setOldDepartment(null);
        }

        // New Department (optional)
        if (history.getNewDepartment() != null
                && history.getNewDepartment().getId() != null) {
            Optional<Department> newDept = departmentRepository.findById(
                    history.getNewDepartment().getId());
            if (newDept.isEmpty()) {
                return response.send("New department not found with id: "
                        + history.getNewDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            history.setNewDepartment(newDept.get());
        } else {
            history.setNewDepartment(null);
        }

        // Old Designation (optional)
        if (history.getOldDesignation() != null
                && history.getOldDesignation().getId() != null) {
            Optional<Designation> oldDesig = designationRepository.findById(
                    history.getOldDesignation().getId());
            if (oldDesig.isEmpty()) {
                return response.send("Old designation not found with id: "
                        + history.getOldDesignation().getId(), null, HttpStatus.NOT_FOUND);
            }
            history.setOldDesignation(oldDesig.get());
        } else {
            history.setOldDesignation(null);
        }

        // New Designation (optional)
        if (history.getNewDesignation() != null
                && history.getNewDesignation().getId() != null) {
            Optional<Designation> newDesig = designationRepository.findById(
                    history.getNewDesignation().getId());
            if (newDesig.isEmpty()) {
                return response.send("New designation not found with id: "
                        + history.getNewDesignation().getId(), null, HttpStatus.NOT_FOUND);
            }
            history.setNewDesignation(newDesig.get());
        } else {
            history.setNewDesignation(null);
        }

        return null; // null = no error
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addJobHistory(EmployeeJobHistory history) {
        ResponseEntity<ResponseWrapper> validationError = resolveAndSetFKs(history);
        if (validationError != null) return validationError;

        EmployeeJobHistory saved = jobHistoryRepository.save(history);
        return response.send("Job history record created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getJobHistoryById(Long id) {
        Optional<EmployeeJobHistory> history = jobHistoryRepository.findById(id);
        if (history.isEmpty()) {
            return response.send("Job history record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Job history fetched successfully", history.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllJobHistory(String sortBy,
                                                             String sortDirection) {
        Specification<EmployeeJobHistory> spec = Specification
                .where(EmployeeJobHistorySpecification.sortByField(sortBy, sortDirection));

        List<EmployeeJobHistory> records = jobHistoryRepository.findAll(spec);
        return response.send("Job history fetched successfully", records, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateJobHistory(Long id,
                                                             EmployeeJobHistory updatedHistory) {
        Optional<EmployeeJobHistory> existing = jobHistoryRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Job history record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }

        EmployeeJobHistory history = existing.get();
        history.setChangeType(updatedHistory.getChangeType());
        history.setEffectiveDate(updatedHistory.getEffectiveDate());

        // Re-use FK validation helper on a temporary object
        updatedHistory.setEmployee(updatedHistory.getEmployee() != null
                ? updatedHistory.getEmployee() : history.getEmployee());

        ResponseEntity<ResponseWrapper> validationError = resolveAndSetFKs(updatedHistory);
        if (validationError != null) return validationError;

        history.setEmployee(updatedHistory.getEmployee());
        history.setOldDepartment(updatedHistory.getOldDepartment());
        history.setNewDepartment(updatedHistory.getNewDepartment());
        history.setOldDesignation(updatedHistory.getOldDesignation());
        history.setNewDesignation(updatedHistory.getNewDesignation());

        EmployeeJobHistory saved = jobHistoryRepository.save(history);
        return response.send("Job history record updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteJobHistory(Long id) {
        Optional<EmployeeJobHistory> history = jobHistoryRepository.findById(id);
        if (history.isEmpty()) {
            return response.send("Job history record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        jobHistoryRepository.deleteById(id);
        return response.send("Job history record deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterJobHistory(Long employeeId,
                                                             String changeType,
                                                             LocalDate effectiveDateFrom,
                                                             LocalDate effectiveDateTo,
                                                             String sortBy,
                                                             String sortDirection) {
        Specification<EmployeeJobHistory> spec = Specification
                .where(EmployeeJobHistorySpecification.hasEmployee(employeeId))
                .and(EmployeeJobHistorySpecification.hasChangeType(changeType))
                .and(EmployeeJobHistorySpecification.effectiveDateFrom(effectiveDateFrom))
                .and(EmployeeJobHistorySpecification.effectiveDateTo(effectiveDateTo))
                .and(EmployeeJobHistorySpecification.sortByField(sortBy, sortDirection));

        List<EmployeeJobHistory> records = jobHistoryRepository.findAll(spec);
        return response.send("Job history filtered successfully", records, HttpStatus.OK);
    }

}
