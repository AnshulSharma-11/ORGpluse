package com.orgpluse.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.entities.PerformanceReview;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.repositories.PerformanceReviewRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.PerformanceReviewSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceReviewService {

    @Autowired
    private PerformanceReviewRepository reviewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UniversalResponse response;

    // ── FK resolution helper — used by both add and update ───────────────────

    private ResponseEntity<ResponseWrapper> resolveFKs(PerformanceReview review) {

        // Employee being reviewed (required)
        if (review.getEmployee() == null || review.getEmployee().getId() == null) {
            return response.send("Employee (reviewee) is required", null, HttpStatus.BAD_REQUEST);
        }
        Optional<Employee> employee = employeeRepository.findById(
                review.getEmployee().getId());
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: "
                    + review.getEmployee().getId(), null, HttpStatus.NOT_FOUND);
        }
        review.setEmployee(employee.get());

        // Reviewer (optional — review may not yet be assigned)
        if (review.getReviewer() != null && review.getReviewer().getId() != null) {
            if (review.getReviewer().getId().equals(review.getEmployee().getId())) {
                return response.send("An employee cannot review themselves",
                        null, HttpStatus.BAD_REQUEST);
            }
            Optional<Employee> reviewer = employeeRepository.findById(
                    review.getReviewer().getId());
            if (reviewer.isEmpty()) {
                return response.send("Reviewer (Employee) not found with id: "
                        + review.getReviewer().getId(), null, HttpStatus.NOT_FOUND);
            }
            review.setReviewer(reviewer.get());
        } else {
            review.setReviewer(null);
        }

        return null; // null = no validation error
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addReview(PerformanceReview review) {
        ResponseEntity<ResponseWrapper> error = resolveFKs(review);
        if (error != null) return error;

        // Default status to PENDING if omitted
        if (review.getStatus() == null || review.getStatus().isBlank()) {
            review.setStatus("PENDING");
        }

        PerformanceReview saved = reviewRepository.save(review);
        return response.send("Performance review created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getReviewById(Long id) {
        Optional<PerformanceReview> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            return response.send("Performance review not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        return response.send("Performance review fetched successfully",
                review.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllReviews(String sortBy, String sortDirection) {
        Specification<PerformanceReview> spec = Specification
                .where(PerformanceReviewSpecification.sortByField(sortBy, sortDirection));

        List<PerformanceReview> reviews = reviewRepository.findAll(spec);
        return response.send("Performance reviews fetched successfully", reviews, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateReview(Long id,
                                                         PerformanceReview updatedReview) {
        Optional<PerformanceReview> existing = reviewRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Performance review not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }

        PerformanceReview review = existing.get();
        review.setCycleName(updatedReview.getCycleName());
        review.setStartDate(updatedReview.getStartDate());
        review.setEndDate(updatedReview.getEndDate());
        review.setCriteriaRatings(updatedReview.getCriteriaRatings());
        review.setOverallRating(updatedReview.getOverallRating());
        review.setStatus(updatedReview.getStatus());
        review.setSubmittedAt(updatedReview.getSubmittedAt());

        // Re-resolve FKs from the updated payload
        ResponseEntity<ResponseWrapper> error = resolveFKs(updatedReview);
        if (error != null) return error;

        review.setEmployee(updatedReview.getEmployee());
        review.setReviewer(updatedReview.getReviewer());

        PerformanceReview saved = reviewRepository.save(review);
        return response.send("Performance review updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteReview(Long id) {
        Optional<PerformanceReview> review = reviewRepository.findById(id);
        if (review.isEmpty()) {
            return response.send("Performance review not found with id: " + id,
                    null, HttpStatus.NOT_FOUND);
        }
        reviewRepository.deleteById(id);
        return response.send("Performance review deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterReviews(Long employeeId,
                                                          Long reviewerId,
                                                          String status,
                                                          String cycleName,
                                                          LocalDate startDate,
                                                          LocalDate endDate,
                                                          String sortBy,
                                                          String sortDirection) {
        Specification<PerformanceReview> spec = Specification
                .where(PerformanceReviewSpecification.hasEmployee(employeeId))
                .and(PerformanceReviewSpecification.hasReviewer(reviewerId))
                .and(PerformanceReviewSpecification.hasStatus(status))
                .and(PerformanceReviewSpecification.hasCycleName(cycleName))
                .and(PerformanceReviewSpecification.startDateFrom(startDate))
                .and(PerformanceReviewSpecification.endDateTo(endDate))
                .and(PerformanceReviewSpecification.sortByField(sortBy, sortDirection));

        List<PerformanceReview> reviews = reviewRepository.findAll(spec);
        return response.send("Performance reviews filtered successfully", reviews, HttpStatus.OK);
    }

}
