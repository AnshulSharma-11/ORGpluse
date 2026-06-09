package com.orgpluse.services;

import com.orgpluse.common.PageResponse;
import com.orgpluse.common.PageableUtils;
import com.orgpluse.entities.Employee;
import com.orgpluse.entities.TimeRecord;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.exception.BadRequestException;
import com.orgpluse.exception.ResourceNotFoundException;
import com.orgpluse.repositories.TimeRecordRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.TimeRecordSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class TimeRecordService {

    private static final Set<String> ALLOWED_SORTS =
            Set.of("id", "date", "status", "hoursWorked", "checkIn", "checkOut", "createdAt");

    @Autowired private TimeRecordRepository timeRecordRepository;
    @Autowired private EmployeeRepository   employeeRepository;
    @Autowired private UniversalResponse    response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addTimeRecord(TimeRecord timeRecord) {
        if (timeRecord.getEmployee() == null || timeRecord.getEmployee().getId() == null)
            throw new BadRequestException("Employee is required");

        Employee employee = employeeRepository.findById(timeRecord.getEmployee().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee",
                        timeRecord.getEmployee().getId()));
        timeRecord.setEmployee(employee);

        TimeRecord saved = timeRecordRepository.save(timeRecord);
        return response.send("Time record created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getTimeRecordById(Long id) {
        TimeRecord timeRecord = timeRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time record", id));
        return response.send("Time record fetched successfully", timeRecord, HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllTimeRecords(String sortBy,
                                                              String sortDirection,
                                                              Integer page,
                                                              Integer size) {
        Pageable pageable = PageableUtils.of(page, size, sortBy, sortDirection, ALLOWED_SORTS);
        Page<TimeRecord> result = timeRecordRepository.findAll(pageable);
        return response.send("Time records fetched successfully",
                new PageResponse<>(result), HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateTimeRecord(Long id, TimeRecord updatedRecord) {
        TimeRecord record = timeRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time record", id));
        record.setCheckIn(updatedRecord.getCheckIn());
        record.setCheckOut(updatedRecord.getCheckOut());
        record.setDate(updatedRecord.getDate());
        record.setStatus(updatedRecord.getStatus());
        record.setHoursWorked(updatedRecord.getHoursWorked());
        record.setRemarks(updatedRecord.getRemarks());

        if (updatedRecord.getEmployee() != null && updatedRecord.getEmployee().getId() != null) {
            Employee emp = employeeRepository.findById(updatedRecord.getEmployee().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee",
                            updatedRecord.getEmployee().getId()));
            record.setEmployee(emp);
        }

        TimeRecord saved = timeRecordRepository.save(record);
        return response.send("Time record updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteTimeRecord(Long id) {
        if (!timeRecordRepository.existsById(id))
            throw new ResourceNotFoundException("Time record", id);
        timeRecordRepository.deleteById(id);
        return response.send("Time record deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterTimeRecords(Long employeeId,
                                                              String status,
                                                              LocalDate dateFrom,
                                                              LocalDate dateTo,
                                                              String sortBy,
                                                              String sortDirection,
                                                              Integer page,
                                                              Integer size) {
        Pageable pageable = PageableUtils.of(page, size, sortBy, sortDirection, ALLOWED_SORTS);
        Specification<TimeRecord> spec = Specification
                .where(TimeRecordSpecification.hasEmployee(employeeId))
                .and(TimeRecordSpecification.hasStatus(status))
                .and(TimeRecordSpecification.dateFrom(dateFrom))
                .and(TimeRecordSpecification.dateTo(dateTo));
        Page<TimeRecord> result = timeRecordRepository.findAll(spec, pageable);
        return response.send("Time records filtered successfully",
                new PageResponse<>(result), HttpStatus.OK);
    }

}
