package com.orgpluse.payroll.repositories;

import com.orgpluse.payroll.entities.PayrollItem;
import com.orgpluse.payroll.entities.PayrollItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollItemRepository extends JpaRepository<PayrollItem, Long> {

    List<PayrollItem> findByPayrollRecordIdOrderByDisplayOrderAsc(Long payrollRecordId);

    List<PayrollItem> findByPayrollRecordIdAndItemCategory(Long payrollRecordId, PayrollItemCategory category);

    void deleteByPayrollRecordId(Long payrollRecordId);

}
