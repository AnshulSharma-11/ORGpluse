package com.orgpluse.services;

import com.orgpluse.entities.Department;
import com.orgpluse.entities.Employee;
import com.orgpluse.entities.Help;
import com.orgpluse.repositories.DepartmentRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.repositories.HelpRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.HelpSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HelpService {

    @Autowired
    private HelpRepository helpRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UniversalResponse response;

    // ── Shared FK resolution — used by both add and update ───────────────────

    private ResponseEntity<ResponseWrapper> resolveFKs(Help help) {

        // Employee who raised the request (required)
        if (help.getEmployee() == null || help.getEmployee().getId() == null) {
            return response.send("Employee is required", null, HttpStatus.BAD_REQUEST);
        }
        Optional<Employee> employee = employeeRepository.findById(
                help.getEmployee().getId());
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: "
                    + help.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
        }
        help.setEmployee(employee.get());

        // Assigned HR/Admin employee (optional)
        if (help.getAssignedTo() != null && help.getAssignedTo().getId() != null) {
            Optional<Employee> assignee = employeeRepository.findById(
                    help.getAssignedTo().getId());
            if (assignee.isEmpty()) {
                return response.send("Assigned employee not found with id: "
                        + help.getAssignedTo().getId(), null, HttpStatus.NOT_FOUND);
            }
            help.setAssignedTo(assignee.get());
        } else {
            help.setAssignedTo(null);
        }

        // Current department (optional — relevant for transfer requests)
        if (help.getCurrentDepartment() != null
                && help.getCurrentDepartment().getId() != null) {
            Optional<Department> currentDept = departmentRepository.findById(
                    help.getCurrentDepartment().getId());
            if (currentDept.isEmpty()) {
                return response.send("Current department not found with id: "
                        + help.getCurrentDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            help.setCurrentDepartment(currentDept.get());
        } else {
            help.setCurrentDepartment(null);
        }

        // Requested department (optional — relevant for transfer requests)
        if (help.getRequestedDepartment() != null
                && help.getRequestedDepartment().getId() != null) {
            Optional<Department> requestedDept = departmentRepository.findById(
                    help.getRequestedDepartment().getId());
            if (requestedDept.isEmpty()) {
                return response.send("Requested department not found with id: "
                        + help.getRequestedDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            help.setRequestedDepartment(requestedDept.get());
        } else {
            help.setRequestedDepartment(null);
        }

        return null; // null = no error
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addHelp(Help help) {
        ResponseEntity<ResponseWrapper> error = resolveFKs(help);
        if (error != null) return error;

        // Default status to OPEN for new requests
        if (help.getStatus() == null || help.getStatus().isBlank()) {
            help.setStatus("OPEN");
        }

        Help saved = helpRepository.save(help);
        return response.send("Help request submitted successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getHelpById(Long id) {
        Optional<Help> help = helpRepository.findById(id);
        if (help.isEmpty()) {
            return response.send("Help request not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Help request fetched successfully", help.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllHelp(String search,
                                                       String sortBy,
                                                       String sortDirection) {
        Specification<Help> spec = Specification
                .where(HelpSpecification.searchBySubject(search))
                .and(HelpSpecification.sortByField(sortBy, sortDirection));

        List<Help> helpList = helpRepository.findAll(spec);
        return response.send("Help requests fetched successfully", helpList, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateHelp(Long id, Help updatedHelp) {
        Optional<Help> existing = helpRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Help request not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }

        Help help = existing.get();
        help.setRequestType(updatedHelp.getRequestType());
        help.setSubject(updatedHelp.getSubject());
        help.setDescription(updatedHelp.getDescription());
        help.setPriority(updatedHelp.getPriority());
        help.setResolutionNotes(updatedHelp.getResolutionNotes());

        // Auto-set resolvedAt timestamp when status transitions to RESOLVED or CLOSED
        String newStatus = updatedHelp.getStatus();
        help.setStatus(newStatus);
        if (("RESOLVED".equalsIgnoreCase(newStatus) || "CLOSED".equalsIgnoreCase(newStatus))
                && help.getResolvedAt() == null) {
            help.setResolvedAt(LocalDateTime.now());
        }

        // Re-resolve all FKs from the updated payload
        ResponseEntity<ResponseWrapper> error = resolveFKs(updatedHelp);
        if (error != null) return error;

        help.setEmployee(updatedHelp.getEmployee());
        help.setAssignedTo(updatedHelp.getAssignedTo());
        help.setCurrentDepartment(updatedHelp.getCurrentDepartment());
        help.setRequestedDepartment(updatedHelp.getRequestedDepartment());

        Help saved = helpRepository.save(help);
        return response.send("Help request updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteHelp(Long id) {
        Optional<Help> help = helpRepository.findById(id);
        if (help.isEmpty()) {
            return response.send("Help request not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        helpRepository.deleteById(id);
        return response.send("Help request deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterHelp(String requestType,
                                                       String status,
                                                       String priority,
                                                       Long employeeId,
                                                       Long assignedToId,
                                                       Long currentDepartmentId,
                                                       Long requestedDepartmentId,
                                                       String search,
                                                       String sortBy,
                                                       String sortDirection) {
        Specification<Help> spec = Specification
                .where(HelpSpecification.hasRequestType(requestType))
                .and(HelpSpecification.hasStatus(status))
                .and(HelpSpecification.hasPriority(priority))
                .and(HelpSpecification.hasEmployee(employeeId))
                .and(HelpSpecification.hasAssignedTo(assignedToId))
                .and(HelpSpecification.hasCurrentDepartment(currentDepartmentId))
                .and(HelpSpecification.hasRequestedDepartment(requestedDepartmentId))
                .and(HelpSpecification.searchBySubject(search))
                .and(HelpSpecification.sortByField(sortBy, sortDirection));

        List<Help> helpList = helpRepository.findAll(spec);
        return response.send("Help requests filtered successfully", helpList, HttpStatus.OK);
    }

}
