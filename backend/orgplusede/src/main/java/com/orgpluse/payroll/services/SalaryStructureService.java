package com.orgpluse.payroll.services;

import com.orgpluse.entities.Employee;
import com.orgpluse.payroll.dto.SalaryStructureRequest;
import com.orgpluse.payroll.entities.SalaryStructure;
import com.orgpluse.payroll.repositories.SalaryStructureRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryStructureService {

    @Autowired private SalaryStructureRepository salaryStructureRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> create(SalaryStructureRequest req) {
        Employee emp = employeeRepository.findById(req.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));
        SalaryStructure ss = mapFromRequest(req, new SalaryStructure());
        ss.setEmployee(emp);
        SalaryStructure saved = salaryStructureRepository.save(ss);
        return response.send("Salary structure created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getById(Long id) {
        SalaryStructure ss = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure", id));
        return response.send("Salary structure fetched successfully", ss, HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getByEmployee(Long employeeId) {
        List<SalaryStructure> list =
                salaryStructureRepository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId);
        return response.send("Salary structures fetched successfully", list, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> update(Long id, SalaryStructureRequest req) {
        SalaryStructure existing = salaryStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary structure", id));
        SalaryStructure ss = mapFromRequest(req, existing);
        if (req.getEmployeeId() != null) {
            Employee emp = employeeRepository.findById(req.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", req.getEmployeeId()));
            ss.setEmployee(emp);
        }
        SalaryStructure saved = salaryStructureRepository.save(ss);
        return response.send("Salary structure updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> delete(Long id) {
        if (!salaryStructureRepository.existsById(id))
            throw new ResourceNotFoundException("Salary structure", id);
        salaryStructureRepository.deleteById(id);
        return response.send("Salary structure deleted successfully", null, HttpStatus.OK);
    }

    // ── HELPER ────────────────────────────────────────────────────────────────

    private SalaryStructure mapFromRequest(SalaryStructureRequest req, SalaryStructure ss) {
        ss.setBasicSalary(req.getBasicSalary());
        ss.setHra(req.getHra());
        ss.setConveyanceAllowance(req.getConveyanceAllowance());
        ss.setMedicalAllowance(req.getMedicalAllowance());
        ss.setSpecialAllowance(req.getSpecialAllowance());
        ss.setOtherAllowances(req.getOtherAllowances());
        ss.setPfEmployee(req.getPfEmployee());
        ss.setPfEmployer(req.getPfEmployer());
        ss.setProfessionalTax(req.getProfessionalTax());
        ss.setEsicEmployee(req.getEsicEmployee());
        ss.setEffectiveFrom(req.getEffectiveFrom());
        ss.setEffectiveTo(req.getEffectiveTo());
        ss.setCurrency(req.getCurrency());
        return ss;
    }

}
