package com.orgpluse.controllers;

import com.orgpluse.entities.Designation;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.DesignationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    // POST /api/v1/admin/designations
    @PostMapping("/designations")
    public ResponseEntity<ResponseWrapper> addDesignation(@RequestBody Designation designation) {
        return designationService.addDesignation(designation);
    }

    // PUT /api/v1/admin/designations/{id}
    @PutMapping("/designations/{id}")
    public ResponseEntity<ResponseWrapper> updateDesignation(@PathVariable Long id,
                                                              @RequestBody Designation designation) {
        return designationService.updateDesignation(id, designation);
    }

    // DELETE /api/v1/admin/designations/{id}
    @DeleteMapping("/designations/{id}")
    public ResponseEntity<ResponseWrapper> deleteDesignation(@PathVariable Long id) {
        return designationService.deleteDesignation(id);
    }

    // GET /api/v1/admin/designations/{id}
    @GetMapping("/designations/{id}")
    public ResponseEntity<ResponseWrapper> getDesignationById(@PathVariable Long id) {
        return designationService.getDesignationById(id);
    }

    // GET /api/v1/admin/designations?search=&sortBy=&sortDirection=
    @GetMapping("/designations")
    public ResponseEntity<ResponseWrapper> getAllDesignations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return designationService.getAllDesignations(search, sortBy, sortDirection);
    }

    // GET /api/v1/admin/designations/filter?level=&sortBy=&sortDirection=
    @GetMapping("/designations/filter")
    public ResponseEntity<ResponseWrapper> filterDesignations(
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return designationService.filterDesignations(level, sortBy, sortDirection);
    }

}
