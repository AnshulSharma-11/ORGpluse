package com.orgpluse.services;

import com.orgpluse.entities.Designation;
import com.orgpluse.repositories.DesignationRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.DesignationSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignationService {

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addDesignation(Designation designation) {
        Designation saved = designationRepository.save(designation);
        return response.send("Designation created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getDesignationById(Long id) {
        Optional<Designation> designation = designationRepository.findById(id);
        if (designation.isEmpty()) {
            return response.send("Designation not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        return response.send("Designation fetched successfully", designation.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllDesignations(String search,
                                                               String sortBy,
                                                               String sortDirection) {
        Specification<Designation> spec = Specification
                .where(DesignationSpecification.searchByTitle(search))
                .and(DesignationSpecification.sortByField(sortBy, sortDirection));

        List<Designation> designations = designationRepository.findAll(spec);
        return response.send("Designations fetched successfully", designations, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateDesignation(Long id, Designation updatedDesignation) {
        Optional<Designation> existing = designationRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Designation not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Designation designation = existing.get();
        designation.setTitle(updatedDesignation.getTitle());
        designation.setLevel(updatedDesignation.getLevel());

        Designation saved = designationRepository.save(designation);
        return response.send("Designation updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteDesignation(Long id) {
        Optional<Designation> designation = designationRepository.findById(id);
        if (designation.isEmpty()) {
            return response.send("Designation not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        designationRepository.deleteById(id);
        return response.send("Designation deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterDesignations(Integer level,
                                                               String sortBy,
                                                               String sortDirection) {
        Specification<Designation> spec = Specification
                .where(DesignationSpecification.hasLevel(level))
                .and(DesignationSpecification.sortByField(sortBy, sortDirection));

        List<Designation> designations = designationRepository.findAll(spec);
        return response.send("Designations filtered successfully", designations, HttpStatus.OK);
    }

}
