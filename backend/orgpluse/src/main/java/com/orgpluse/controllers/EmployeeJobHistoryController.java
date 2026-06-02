package com.orgpluse.controllers;

import com.orgpluse.entities.EmployeeJobHistory;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.EmployeeJobHistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class EmployeeJobHistoryController {

    @Autowired
    private EmployeeJobHistoryService jobHistoryService;

    // POST /api/v1/admin/job-history
    @PostMapping("/job-history")
    public ResponseEntity<ResponseWrapper> addJobHistory(
            @RequestBody EmployeeJobHistory history) {
        return jobHistoryService.addJobHistory(history);
    }

    // PUT /api/v1/admin/job-history/{id}
    @PutMapping("/job-history/{id}")
    public ResponseEntity<ResponseWrapper> updateJobHistory(
            @PathVariable Long id,
            @RequestBody EmployeeJobHistory history) {
        return jobHistoryService.updateJobHistory(id, history);
    }

    // DELETE /api/v1/admin/job-history/{id}
    @DeleteMapping("/job-history/{id}")
    public ResponseEntity<ResponseWrapper> deleteJobHistory(@PathVariable Long id) {
        return jobHistoryService.deleteJobHistory(id);
    }

    // GET /api/v1/admin/job-history/{id}
    @GetMapping("/job-history/{id}")
    public ResponseEntity<ResponseWrapper> getJobHistoryById(@PathVariable Long id) {
        return jobHistoryService.getJobHistoryById(id);
    }

    // GET /api/v1/admin/job-history?sortBy=&sortDirection=
    @GetMapping("/job-history")
    public ResponseEntity<ResponseWrapper> getAllJobHistory(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return jobHistoryService.getAllJobHistory(sortBy, sortDirection);
    }

    // GET /api/v1/admin/job-history/filter?employeeId=&changeType=
    //                                     &effectiveDateFrom=&effectiveDateTo=
    //                                     &sortBy=&sortDirection=
    @GetMapping("/job-history/filter")
    public ResponseEntity<ResponseWrapper> filterJobHistory(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDateTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return jobHistoryService.filterJobHistory(employeeId, changeType,
                effectiveDateFrom, effectiveDateTo, sortBy, sortDirection);
    }

}
