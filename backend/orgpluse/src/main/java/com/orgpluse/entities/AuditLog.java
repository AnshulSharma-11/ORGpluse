package com.orgpluse.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // CREATE / UPDATE / DELETE / LOGIN / LOGOUT / APPROVE / REJECT
    @NotBlank(message = "Action is required")
    @Column(nullable = false)
    private String action;

    // e.g. "Employee", "Leave", "Department"
    @NotBlank(message = "Entity type is required")
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @NotNull(message = "Entity ID is required")
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    // JSON string — before/after snapshot: {"before":{...},"after":{...}}
    @Column(columnDefinition = "TEXT")
    private String changes;

    @Column(name = "ip_address")
    private String ipAddress;

    // Auto-populated on creation — audit logs are immutable so no @LastModifiedDate
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    // ── Employee who performed the action
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"department", "designation", "branch",
            "manager", "createdAt", "updatedAt", "passwordHash"})
    private Employee user;

}
