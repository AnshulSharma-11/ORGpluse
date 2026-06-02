package com.orgpluse.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.orgpluse.entities.EmployeeJobHistory;

import java.time.LocalDate;

public class EmployeeJobHistorySpecification {

    public static Specification<EmployeeJobHistory> hasEmployee(Long employeeId) {
        return (root, query, cb) -> {
            if (employeeId == null) return null;
            return cb.equal(root.get("employee").get("id"), employeeId);
        };
    }

    public static Specification<EmployeeJobHistory> hasChangeType(String changeType) {
        return (root, query, cb) -> {
            if (changeType == null || changeType.isBlank()) return null;
            return cb.equal(cb.lower(root.get("changeType")), changeType.toLowerCase());
        };
    }

    public static Specification<EmployeeJobHistory> effectiveDateFrom(LocalDate from) {
        return (root, query, cb) -> {
            if (from == null) return null;
            return cb.greaterThanOrEqualTo(root.get("effectiveDate"), from);
        };
    }

    public static Specification<EmployeeJobHistory> effectiveDateTo(LocalDate to) {
        return (root, query, cb) -> {
            if (to == null) return null;
            return cb.lessThanOrEqualTo(root.get("effectiveDate"), to);
        };
    }

    public static Specification<EmployeeJobHistory> sortByField(String sortBy,
                                                                  String sortDirection) {
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
