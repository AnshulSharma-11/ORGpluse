package com.orgpluse.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.entities.TimeRecord;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.repositories.TimeRecordRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.TimeRecordSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TimeRecordService {

    @Autowired
    private TimeRecordRepository timeRecordRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addTimeRecord(TimeRecord timeRecord) {
        if (timeRecord.getEmployee() == null || timeRecord.getEmployee().getId() == null) {
            return response.send("Employee is required", null, HttpStatus.BAD_REQUEST);
        }

        Optional<Employee> employee = employeeRepository.findById(
                timeRecord.getEmployee().getId());
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: "
                    + timeRecord.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
        }
        timeRecord.setEmployee(employee.get());

        TimeRecord saved = timeRecordRepository.save(timeRecord);
        return response.send("Time record created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getTimeRecordById(Long id) {
        Optional<TimeRecord> timeRecord = timeRecordRepository.findById(id);
        if (timeRecord.isEmpty()) {
            return response.send("Time record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Time record fetched successfully", timeRecord.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllTimeRecords(String sortBy,
                                                              String sortDirection) {
        Specification<TimeRecord> spec = Specification
                .where(TimeRecordSpecification.sortByField(sortBy, sortDirection));

        List<TimeRecord> records = timeRecordRepository.findAll(spec);
        return response.send("Time records fetched successfully", records, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateTimeRecord(Long id,
                                                             TimeRecord updatedRecord) {
        Optional<TimeRecord> existing = timeRecordRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Time record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }

        TimeRecord record = existing.get();
        record.setCheckIn(updatedRecord.getCheckIn());
        record.setCheckOut(updatedRecord.getCheckOut());
        record.setDate(updatedRecord.getDate());
        record.setStatus(updatedRecord.getStatus());
        record.setHoursWorked(updatedRecord.getHoursWorked());
        record.setRemarks(updatedRecord.getRemarks());

        if (updatedRecord.getEmployee() != null
                && updatedRecord.getEmployee().getId() != null) {
            Optional<Employee> emp = employeeRepository.findById(
                    updatedRecord.getEmployee().getId());
            if (emp.isEmpty()) {
                return response.send("Employee not found with id: "
                        + updatedRecord.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
            }
            record.setEmployee(emp.get());
        }

        TimeRecord saved = timeRecordRepository.save(record);
        return response.send("Time record updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteTimeRecord(Long id) {
        Optional<TimeRecord> timeRecord = timeRecordRepository.findById(id);
        if (timeRecord.isEmpty()) {
            return response.send("Time record not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        timeRecordRepository.deleteById(id);
        return response.send("Time record deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterTimeRecords(Long employeeId,
                                                              String status,
                                                              LocalDate dateFrom,
                                                              LocalDate dateTo,
                                                              String sortDirection) {
        Specification<TimeRecord> spec = Specification
                .where(TimeRecordSpecification.hasEmployee(employeeId))
                .and(TimeRecordSpecification.hasStatus(status))
                .and(TimeRecordSpecification.dateFrom(dateFrom))
                .and(TimeRecordSpecification.dateTo(dateTo))
                .and(TimeRecordSpecification.sortByDate(sortDirection));

        List<TimeRecord> records = timeRecordRepository.findAll(spec);
        return response.send("Time records filtered successfully", records, HttpStatus.OK);
    }

}
