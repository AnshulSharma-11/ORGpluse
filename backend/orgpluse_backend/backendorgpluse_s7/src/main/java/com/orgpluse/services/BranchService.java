package com.orgpluse.services;

import com.orgpluse.entities.Branch;
import com.orgpluse.repositories.BranchRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.BranchSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addBranch(Branch branch) {
        Branch saved = branchRepository.save(branch);
        return response.send("Branch created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getBranchById(Long id) {
        Optional<Branch> branch = branchRepository.findById(id);
        if (branch.isEmpty()) {
            return response.send("Branch not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        return response.send("Branch fetched successfully", branch.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllBranches(String search,
                                                          String sortBy,
                                                          String sortDirection) {
        Specification<Branch> spec = Specification
                .where(BranchSpecification.searchByName(search))
                .and(BranchSpecification.sortByField(sortBy, sortDirection));

        List<Branch> branches = branchRepository.findAll(spec);
        return response.send("Branches fetched successfully", branches, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateBranch(Long id, Branch updatedBranch) {
        Optional<Branch> existing = branchRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Branch not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Branch branch = existing.get();
        branch.setName(updatedBranch.getName());
        branch.setCity(updatedBranch.getCity());
        branch.setCountry(updatedBranch.getCountry());

        Branch saved = branchRepository.save(branch);
        return response.send("Branch updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteBranch(Long id) {
        Optional<Branch> branch = branchRepository.findById(id);
        if (branch.isEmpty()) {
            return response.send("Branch not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        branchRepository.deleteById(id);
        return response.send("Branch deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterBranches(String city,
                                                           String country,
                                                           String sortBy,
                                                           String sortDirection) {
        Specification<Branch> spec = Specification
                .where(BranchSpecification.hasCity(city))
                .and(BranchSpecification.hasCountry(country))
                .and(BranchSpecification.sortByField(sortBy, sortDirection));

        List<Branch> branches = branchRepository.findAll(spec);
        return response.send("Branches filtered successfully", branches, HttpStatus.OK);
    }

}
