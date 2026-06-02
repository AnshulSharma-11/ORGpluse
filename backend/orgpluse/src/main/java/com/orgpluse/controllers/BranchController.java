package com.orgpluse.controllers;

import com.orgpluse.entities.Branch;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.services.BranchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin("*")
public class BranchController {

    @Autowired
    private BranchService branchService;

    // POST /api/v1/admin/branches
    @PostMapping("/branches")
    public ResponseEntity<ResponseWrapper> addBranch(@RequestBody Branch branch) {
        return branchService.addBranch(branch);
    }

    // PUT /api/v1/admin/branches/{id}
    @PutMapping("/branches/{id}")
    public ResponseEntity<ResponseWrapper> updateBranch(@PathVariable Long id,
                                                         @RequestBody Branch branch) {
        return branchService.updateBranch(id, branch);
    }

    // DELETE /api/v1/admin/branches/{id}
    @DeleteMapping("/branches/{id}")
    public ResponseEntity<ResponseWrapper> deleteBranch(@PathVariable Long id) {
        return branchService.deleteBranch(id);
    }

    // GET /api/v1/admin/branches/{id}
    @GetMapping("/branches/{id}")
    public ResponseEntity<ResponseWrapper> getBranchById(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    // GET /api/v1/admin/branches?search=&sortBy=&sortDirection=
    @GetMapping("/branches")
    public ResponseEntity<ResponseWrapper> getAllBranches(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return branchService.getAllBranches(search, sortBy, sortDirection);
    }

    // GET /api/v1/admin/branches/filter?city=&country=&sortBy=&sortDirection=
    @GetMapping("/branches/filter")
    public ResponseEntity<ResponseWrapper> filterBranches(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        return branchService.filterBranches(city, country, sortBy, sortDirection);
    }

}
