package com.orgpluse.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "employee_job_history")
@EntityListeners(AuditingEntityListener.class)
public class EmployeeJobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // DEPARTMENT_CHANGE / DESIGNATION_CHANGE
    @NotBlank(message = "Change type is required")
    @Column(name = "change_type", nullable = false)
    private String changeType;

    @NotNull(message = "Effective date is required")
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // ── Employee whose job changed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"department", "designation", "branch",
            "manager", "createdAt", "updatedAt", "passwordHash"})
    private Employee employee;

    // ── Department before the change (nullable — may not apply for designation change)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_department_id")
    @JsonIgnoreProperties({"manager", "parentDepartment", "employees", "createdAt", "updatedAt"})
    private Department oldDepartment;

    // ── Department after the change
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_department_id")
    @JsonIgnoreProperties({"manager", "parentDepartment", "employees", "createdAt", "updatedAt"})
    private Department newDepartment;

    // ── Designation before the change (nullable — may not apply for department change)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_designation_id")
    private Designation oldDesignation;

    // ── Designation after the change
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_designation_id")
    private Designation newDesignation;

}
