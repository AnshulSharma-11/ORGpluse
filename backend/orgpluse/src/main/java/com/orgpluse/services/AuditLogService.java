package com.orgpluse.services;

import com.orgpluse.entities.AuditLog;
import com.orgpluse.entities.Employee;
import com.orgpluse.repositories.AuditLogRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.AuditLogSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────
    // AuditLog is append-only — no update or delete methods exist.

    public ResponseEntity<ResponseWrapper> addAuditLog(AuditLog auditLog) {
        // Validate user (employee who performed the action) — optional
        if (auditLog.getUser() != null && auditLog.getUser().getId() != null) {
            Optional<Employee> user = employeeRepository.findById(
                    auditLog.getUser().getId());
            if (user.isEmpty()) {
                return response.send("User (Employee) not found with id: "
                        + auditLog.getUser().getId(), null, HttpStatus.NOT_FOUND);
            }
            auditLog.setUser(user.get());
        } else {
            auditLog.setUser(null);
        }

        AuditLog saved = auditLogRepository.save(auditLog);
        return response.send("Audit log recorded successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getAuditLogById(Long id) {
        Optional<AuditLog> auditLog = auditLogRepository.findById(id);
        if (auditLog.isEmpty()) {
            return response.send("Audit log not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Audit log fetched successfully", auditLog.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllAuditLogs(String sortBy,
                                                            String sortDirection) {
        // Default to timestamp desc (most recent first) when no sort given
        Specification<AuditLog> spec = (sortBy != null && !sortBy.isBlank())
                ? Specification.where(AuditLogSpecification.sortByField(sortBy, sortDirection))
                : Specification.where(AuditLogSpecification.sortByTimestamp(sortDirection));

        List<AuditLog> logs = auditLogRepository.findAll(spec);
        return response.send("Audit logs fetched successfully", logs, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterAuditLogs(Long userId,
                                                             String action,
                                                             String entityType,
                                                             Long entityId,
                                                             LocalDate dateFrom,
                                                             LocalDate dateTo,
                                                             String sortBy,
                                                             String sortDirection) {
        Specification<AuditLog> sortSpec = (sortBy != null && !sortBy.isBlank())
                ? AuditLogSpecification.sortByField(sortBy, sortDirection)
                : AuditLogSpecification.sortByTimestamp(sortDirection);

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecification.hasUser(userId))
                .and(AuditLogSpecification.hasAction(action))
                .and(AuditLogSpecification.hasEntityType(entityType))
                .and(AuditLogSpecification.hasEntityId(entityId))
                .and(AuditLogSpecification.timestampFrom(dateFrom))
                .and(AuditLogSpecification.timestampTo(dateTo))
                .and(sortSpec);

        List<AuditLog> logs = auditLogRepository.findAll(spec);
        return response.send("Audit logs filtered successfully", logs, HttpStatus.OK);
    }

}
