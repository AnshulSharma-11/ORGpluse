package com.orgpluse.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.entities.Leave;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.repositories.LeaveRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.LeaveSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── HELPERS ───────────────────────────────────────────────────────────────

    /**
     * Inclusive day count: end - start + 1.
     * e.g. Mon → Wed = 3 days.
     */
    private int calcTotalDays(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addLeave(Leave leave) {
        // Validate applicant employee (required)
        if (leave.getEmployee() == null || leave.getEmployee().getId() == null) {
            return response.send("Employee (applicant) is required", null, HttpStatus.BAD_REQUEST);
        }
        Optional<Employee> employee = employeeRepository.findById(leave.getEmployee().getId());
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: "
                    + leave.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
        }
        leave.setEmployee(employee.get());

        // Validate date range
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            return response.send("Start date and end date are required", null, HttpStatus.BAD_REQUEST);
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            return response.send("End date cannot be before start date", null, HttpStatus.BAD_REQUEST);
        }

        // Auto-calculate totalDays — ignore any client-supplied value
        leave.setTotalDays(calcTotalDays(leave.getStartDate(), leave.getEndDate()));

        // Validate approver (optional)
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getId() != null) {
            Optional<Employee> approver = employeeRepository.findById(
                    leave.getApprovedBy().getId());
            if (approver.isEmpty()) {
                return response.send("Approver (Employee) not found with id: "
                        + leave.getApprovedBy().getId(), null, HttpStatus.NOT_FOUND);
            }
            leave.setApprovedBy(approver.get());
        } else {
            leave.setApprovedBy(null);
        }

        // Default status to PENDING if not provided
        if (leave.getStatus() == null || leave.getStatus().isBlank()) {
            leave.setStatus("PENDING");
        }

        Leave saved = leaveRepository.save(leave);
        return response.send("Leave application submitted successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getLeaveById(Long id) {
        Optional<Leave> leave = leaveRepository.findById(id);
        if (leave.isEmpty()) {
            return response.send("Leave not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        return response.send("Leave fetched successfully", leave.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllLeaves(String sortBy, String sortDirection) {
        Specification<Leave> spec = Specification
                .where(LeaveSpecification.sortByField(sortBy, sortDirection));

        List<Leave> leaves = leaveRepository.findAll(spec);
        return response.send("Leaves fetched successfully", leaves, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateLeave(Long id, Leave updatedLeave) {
        Optional<Leave> existing = leaveRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Leave not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Leave leave = existing.get();
        leave.setLeaveType(updatedLeave.getLeaveType());
        leave.setStartDate(updatedLeave.getStartDate());
        leave.setEndDate(updatedLeave.getEndDate());

        // Auto-calculate totalDays from updated dates
        if (updatedLeave.getStartDate() != null && updatedLeave.getEndDate() != null) {
            if (updatedLeave.getEndDate().isBefore(updatedLeave.getStartDate())) {
                return response.send("End date cannot be before start date", null, HttpStatus.BAD_REQUEST);
            }
            leave.setTotalDays(calcTotalDays(updatedLeave.getStartDate(), updatedLeave.getEndDate()));
        }

        leave.setReason(updatedLeave.getReason());
        leave.setStatus(updatedLeave.getStatus());
        leave.setRejectionNote(updatedLeave.getRejectionNote());

        // Validate and update applicant employee
        if (updatedLeave.getEmployee() != null
                && updatedLeave.getEmployee().getId() != null) {
            Optional<Employee> emp = employeeRepository.findById(
                    updatedLeave.getEmployee().getId());
            if (emp.isEmpty()) {
                return response.send("Employee not found with id: "
                        + updatedLeave.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
            }
            leave.setEmployee(emp.get());
        }

        // Validate and update approver
        if (updatedLeave.getApprovedBy() != null
                && updatedLeave.getApprovedBy().getId() != null) {
            Optional<Employee> approver = employeeRepository.findById(
                    updatedLeave.getApprovedBy().getId());
            if (approver.isEmpty()) {
                return response.send("Approver (Employee) not found with id: "
                        + updatedLeave.getApprovedBy().getId(), null, HttpStatus.NOT_FOUND);
            }
            leave.setApprovedBy(approver.get());
        } else {
            leave.setApprovedBy(null);
        }

        Leave saved = leaveRepository.save(leave);
        return response.send("Leave updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteLeave(Long id) {
        Optional<Leave> leave = leaveRepository.findById(id);
        if (leave.isEmpty()) {
            return response.send("Leave not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        leaveRepository.deleteById(id);
        return response.send("Leave deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterLeaves(Long employeeId,
                                                         String status,
                                                         String leaveType,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         Long approvedById,
                                                         String sortBy,
                                                         String sortDirection) {
        Specification<Leave> spec = Specification
                .where(LeaveSpecification.hasEmployee(employeeId))
                .and(LeaveSpecification.hasStatus(status))
                .and(LeaveSpecification.hasLeaveType(leaveType))
                .and(LeaveSpecification.startDateFrom(startDate))
                .and(LeaveSpecification.endDateTo(endDate))
                .and(LeaveSpecification.hasApprovedBy(approvedById))
                .and(LeaveSpecification.sortByField(sortBy, sortDirection));

        List<Leave> leaves = leaveRepository.findAll(spec);
        return response.send("Leaves filtered successfully", leaves, HttpStatus.OK);
    }

}
