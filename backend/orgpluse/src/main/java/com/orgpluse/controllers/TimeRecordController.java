package com.orgpluse.controllers;

import com.orgpluse.entities.TimeRecord;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.TimeRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class TimeRecordController {

    @Autowired
    private TimeRecordService timeRecordService;

    // POST /api/v1/admin/time-records
    @PostMapping("/time-records")
    public ResponseEntity<ResponseWrapper> addTimeRecord(@RequestBody TimeRecord timeRecord) {
        return timeRecordService.addTimeRecord(timeRecord);
    }

    // PUT /api/v1/admin/time-records/{id}
    @PutMapping("/time-records/{id}")
    public ResponseEntity<ResponseWrapper> updateTimeRecord(@PathVariable Long id, @RequestBody TimeRecord timeRecord) {
        return timeRecordService.updateTimeRecord(id, timeRecord);
    }

    // DELETE /api/v1/admin/time-records/{id}
    @DeleteMapping("/time-records/{id}")
    public ResponseEntity<ResponseWrapper> deleteTimeRecord(@PathVariable Long id) {
        return timeRecordService.deleteTimeRecord(id);
    }

    // GET /api/v1/admin/time-records/{id}
    @GetMapping("/time-records/{id}")
    public ResponseEntity<ResponseWrapper> getTimeRecordById(@PathVariable Long id) {
        return timeRecordService.getTimeRecordById(id);
    }

    // GET /api/v1/admin/time-records?sortBy=&sortDirection=
    @GetMapping("/time-records")
    public ResponseEntity<ResponseWrapper> getAllTimeRecords(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return timeRecordService.getAllTimeRecords(sortBy, sortDirection);
    }

    // GET /api/v1/admin/time-records/filter?employeeId=&status=&dateFrom=&dateTo=&sortDirection=
    @GetMapping("/time-records/filter")
    public ResponseEntity<ResponseWrapper> filterTimeRecords(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String sortDirection) {
        return timeRecordService.filterTimeRecords(employeeId, status,
                dateFrom, dateTo, sortDirection);
    }

}
