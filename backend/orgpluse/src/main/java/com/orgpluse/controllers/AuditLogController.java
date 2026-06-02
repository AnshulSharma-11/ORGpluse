package com.orgpluse.controllers;

import com.orgpluse.entities.AuditLog;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.AuditLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    // POST /api/v1/admin/audit-logs
    // AuditLog records are typically created internally by other services,
    // but this endpoint allows manual logging (e.g. from external systems).
    @PostMapping("/audit-logs")
    public ResponseEntity<ResponseWrapper> addAuditLog(@RequestBody AuditLog auditLog) {
        return auditLogService.addAuditLog(auditLog);
    }

    // GET /api/v1/admin/audit-logs/{id}
    @GetMapping("/audit-logs/{id}")
    public ResponseEntity<ResponseWrapper> getAuditLogById(@PathVariable Long id) {
        return auditLogService.getAuditLogById(id);
    }

    // GET /api/v1/admin/audit-logs?sortBy=&sortDirection=
    // Defaults to timestamp DESC (most recent first)
    @GetMapping("/audit-logs")
    public ResponseEntity<ResponseWrapper> getAllAuditLogs(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return auditLogService.getAllAuditLogs(sortBy, sortDirection);
    }

    // GET /api/v1/admin/audit-logs/filter?userId=&action=&entityType=
    //                                    &entityId=&dateFrom=&dateTo=
    //                                    &sortBy=&sortDirection=
    @GetMapping("/audit-logs/filter")
    public ResponseEntity<ResponseWrapper> filterAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return auditLogService.filterAuditLogs(userId, action, entityType,
                entityId, dateFrom, dateTo, sortBy, sortDirection);
    }

    // ⚠️ NO PUT or DELETE endpoints — AuditLog records are immutable by design.
    // Once written, an audit record must never be modified or removed.

}
