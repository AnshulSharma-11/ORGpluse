package com.orgpluse.repositories;

import com.orgpluse.entities.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {

    // All documents belonging to a specific employee, newest first
    List<EmployeeDocument> findByEmployeeIdOrderByUploadedAtDesc(Long employeeId);

}
