package com.orgpluse.payroll.repositories;

import com.orgpluse.payroll.entities.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends JpaRepository<SalaryStructure, Long> {

    List<SalaryStructure> findByEmployeeIdOrderByEffectiveFromDesc(Long employeeId);

    /**
     * Returns the salary structure that was active on the given reference date.
     * A structure is active when:
     *   effectiveFrom <= referenceDate  AND  (effectiveTo IS NULL OR effectiveTo >= referenceDate)
     */
    @Query("""
           SELECT s FROM SalaryStructure s
           WHERE s.employee.id = :employeeId
             AND s.effectiveFrom <= :referenceDate
             AND (s.effectiveTo IS NULL OR s.effectiveTo >= :referenceDate)
           ORDER BY s.effectiveFrom DESC
           """)
    Optional<SalaryStructure> findActiveStructure(
            @Param("employeeId") Long employeeId,
            @Param("referenceDate") LocalDate referenceDate);

}
