package com.orgpluse.controllers;

import com.orgpluse.entities.Leave;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.LeaveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    // POST /api/v1/admin/leaves
    @PostMapping("/leaves")
    public ResponseEntity<ResponseWrapper> addLeave(@RequestBody Leave leave) {
        return leaveService.addLeave(leave);
    }

    // PUT /api/v1/admin/leaves/{id}
    @PutMapping("/leaves/{id}")
    public ResponseEntity<ResponseWrapper> updateLeave(@PathVariable Long id,
                                                        @RequestBody Leave leave) {
        return leaveService.updateLeave(id, leave);
    }

    // DELETE /api/v1/admin/leaves/{id}
    @DeleteMapping("/leaves/{id}")
    public ResponseEntity<ResponseWrapper> deleteLeave(@PathVariable Long id) {
        return leaveService.deleteLeave(id);
    }

    // GET /api/v1/admin/leaves/{id}
    @GetMapping("/leaves/{id}")
    public ResponseEntity<ResponseWrapper> getLeaveById(@PathVariable Long id) {
        return leaveService.getLeaveById(id);
    }

    // GET /api/v1/admin/leaves?sortBy=&sortDirection=
    @GetMapping("/leaves")
    public ResponseEntity<ResponseWrapper> getAllLeaves(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return leaveService.getAllLeaves(sortBy, sortDirection);
    }

    // GET /api/v1/admin/leaves/filter?employeeId=&status=&leaveType=
    //                               &startDate=&endDate=&approvedBy=
    //                               &sortBy=&sortDirection=
    @GetMapping("/leaves/filter")
    public ResponseEntity<ResponseWrapper> filterLeaves(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long approvedBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return leaveService.filterLeaves(employeeId, status, leaveType,
                startDate, endDate, approvedBy, sortBy, sortDirection);
    }

}
