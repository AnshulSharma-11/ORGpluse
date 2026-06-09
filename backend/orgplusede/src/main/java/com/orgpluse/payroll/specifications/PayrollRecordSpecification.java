package com.orgpluse.payroll.specifications;

import com.orgpluse.payroll.entities.PayrollRecord;
import org.springframework.data.jpa.domain.Specification;

public class PayrollRecordSpecification {

    public static Specification<PayrollRecord> hasEmployee(Long employeeId) {
        return (root, query, cb) -> {
            if (employeeId == null) return null;
            return cb.equal(root.get("employee").get("id"), employeeId);
        };
    }

    public static Specification<PayrollRecord> hasMonth(Integer month) {
        return (root, query, cb) -> {
            if (month == null) return null;
            return cb.equal(root.get("month"), month);
        };
    }

    public static Specification<PayrollRecord> hasYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return null;
            return cb.equal(root.get("year"), year);
        };
    }

    public static Specification<PayrollRecord> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            return cb.equal(cb.lower(root.get("status")), status.toLowerCase());
        };
    }

    public static Specification<PayrollRecord> hasProcessedBy(Long processedById) {
        return (root, query, cb) -> {
            if (processedById == null) return null;
            return cb.equal(root.get("processedBy").get("id"), processedById);
        };
    }

    /** Default sort — newest period first */
    public static Specification<PayrollRecord> sortByYearMonth(String sortDirection) {
        return (root, query, cb) -> {
            if ("desc".equalsIgnoreCase(sortDirection)) {
                query.orderBy(cb.desc(root.get("year")), cb.desc(root.get("month")));
            } else {
                query.orderBy(cb.asc(root.get("year")), cb.asc(root.get("month")));
            }
            return null;
        };
    }

    public static Specification<PayrollRecord> sortByField(String sortBy, String sortDirection) {
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
