package com.orgpluse.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.orgpluse.entities.AuditLog;

import java.time.LocalDate;

public class AuditLogSpecification {

    public static Specification<AuditLog> hasUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) return null;
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<AuditLog> hasAction(String action) {
        return (root, query, cb) -> {
            if (action == null || action.isBlank()) return null;
            return cb.equal(cb.lower(root.get("action")), action.toLowerCase());
        };
    }

    public static Specification<AuditLog> hasEntityType(String entityType) {
        return (root, query, cb) -> {
            if (entityType == null || entityType.isBlank()) return null;
            return cb.equal(cb.lower(root.get("entityType")), entityType.toLowerCase());
        };
    }

    public static Specification<AuditLog> hasEntityId(Long entityId) {
        return (root, query, cb) -> {
            if (entityId == null) return null;
            return cb.equal(root.get("entityId"), entityId);
        };
    }

    // dateFrom/dateTo compare against the LocalDateTime timestamp column
    // by converting the LocalDate boundary to midnight LocalDateTime
    public static Specification<AuditLog> timestampFrom(LocalDate from) {
        return (root, query, cb) -> {
            if (from == null) return null;
            return cb.greaterThanOrEqualTo(root.get("timestamp"), from.atStartOfDay());
        };
    }

    public static Specification<AuditLog> timestampTo(LocalDate to) {
        return (root, query, cb) -> {
            if (to == null) return null;
            return cb.lessThanOrEqualTo(root.get("timestamp"), to.atTime(23, 59, 59));
        };
    }

    public static Specification<AuditLog> sortByTimestamp(String sortDirection) {
        return (root, query, cb) -> {
            if ("asc".equalsIgnoreCase(sortDirection)) {
                query.orderBy(cb.asc(root.get("timestamp")));
            } else {
                query.orderBy(cb.desc(root.get("timestamp")));
            }
            return null;
        };
    }

    public static Specification<AuditLog> sortByField(String sortBy, String sortDirection) {
        return (root, query, cb) -> {
            if (sortBy == null || sortBy.isBlank()) return null;
            if ("desc".equalsIgnoreCase(sortDirection)) {
                query.orderBy(cb.desc(root.get(sortBy)));
            } else {
                query.orderBy(cb.asc(root.get(sortBy)));
            }
            return null;
        };
    }

}
