package com.orgpluse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.orgpluse.entities.EmployeeJobHistory;

@Repository
public interface EmployeeJobHistoryRepository extends JpaRepository<EmployeeJobHistory, Long>,
        JpaSpecificationExecutor<EmployeeJobHistory> {
}
