package com.orgpluse.payroll.repositories;

import com.orgpluse.payroll.entities.PayrollRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRecordRepository
        extends JpaRepository<PayrollRecord, Long>,
                JpaSpecificationExecutor<PayrollRecord> {

    /** Used by duplicate-check before single create */
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    Optional<PayrollRecord> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    /** Used by bulk run to pre-load all already-processed employee IDs for the period */
    @Query("SELECT pr.employee.id FROM PayrollRecord pr WHERE pr.month = :month AND pr.year = :year")
    List<Long> findEmployeeIdsByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    /** All records for a single employee, ordered newest first */
    /** Paginated history — replaces unbounded findByEmployeeIdOrderByYearDescMonthDesc */
    Page<PayrollRecord> findByEmployeeId(Long employeeId, Pageable pageable);

}
