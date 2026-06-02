package com.orgpluse.services;

import com.orgpluse.entities.Branch;
import com.orgpluse.entities.Department;
import com.orgpluse.entities.Designation;
import com.orgpluse.entities.Employee;
import com.orgpluse.repositories.BranchRepository;
import com.orgpluse.repositories.DepartmentRepository;
import com.orgpluse.repositories.DesignationRepository;
import com.orgpluse.repositories.EmployeeRepository;
import com.orgpluse.response_wrapper.ResponseWrapper;
import com.orgpluse.response_wrapper.UniversalResponse;
import com.orgpluse.specifications.EmployeeSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UniversalResponse response;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> addEmployee(Employee employee) {

        // Validate and resolve Department FK
        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Optional<Department> dept = departmentRepository.findById(
                    employee.getDepartment().getId());
            if (dept.isEmpty()) {
                return response.send("Department not found with id: "
                        + employee.getDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setDepartment(dept.get());
        }

        // Validate and resolve Designation FK
        if (employee.getDesignation() != null && employee.getDesignation().getId() != null) {
            Optional<Designation> desig = designationRepository.findById(
                    employee.getDesignation().getId());
            if (desig.isEmpty()) {
                return response.send("Designation not found with id: "
                        + employee.getDesignation().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setDesignation(desig.get());
        }

        // Validate and resolve Branch FK
        if (employee.getBranch() != null && employee.getBranch().getId() != null) {
            Optional<Branch> branch = branchRepository.findById(
                    employee.getBranch().getId());
            if (branch.isEmpty()) {
                return response.send("Branch not found with id: "
                        + employee.getBranch().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setBranch(branch.get());
        }

        // Validate and resolve Manager (self-ref) FK
        if (employee.getManager() != null && employee.getManager().getId() != null) {
            Optional<Employee> manager = employeeRepository.findById(
                    employee.getManager().getId());
            if (manager.isEmpty()) {
                return response.send("Manager (Employee) not found with id: "
                        + employee.getManager().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setManager(manager.get());
        }

        Employee saved = employeeRepository.save(employee);
        return response.send("Employee created successfully", saved, HttpStatus.CREATED);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        return response.send("Employee fetched successfully", employee.get(), HttpStatus.OK);
    }

    public ResponseEntity<ResponseWrapper> getAllEmployees(String search,
                                                            String sortBy,
                                                            String sortDirection) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecification.searchByNameOrEmailOrCode(search))
                .and(EmployeeSpecification.sortByField(sortBy, sortDirection));

        List<Employee> employees = employeeRepository.findAll(spec);
        return response.send("Employees fetched successfully", employees, HttpStatus.OK);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> updateEmployee(Long id, Employee updatedEmployee) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isEmpty()) {
            return response.send("Employee not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }

        Employee employee = existing.get();

        // Update scalar fields
        employee.setEmployeeCode(updatedEmployee.getEmployeeCode());
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setPasswordHash(updatedEmployee.getPasswordHash());
        employee.setPhone(updatedEmployee.getPhone());
        employee.setDob(updatedEmployee.getDob());
        employee.setGender(updatedEmployee.getGender());
        employee.setAddress(updatedEmployee.getAddress());
        employee.setHireDate(updatedEmployee.getHireDate());
        employee.setTerminationDate(updatedEmployee.getTerminationDate());
        employee.setStatus(updatedEmployee.getStatus());

        // Validate and resolve Department FK
        if (updatedEmployee.getDepartment() != null
                && updatedEmployee.getDepartment().getId() != null) {
            Optional<Department> dept = departmentRepository.findById(
                    updatedEmployee.getDepartment().getId());
            if (dept.isEmpty()) {
                return response.send("Department not found with id: "
                        + updatedEmployee.getDepartment().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setDepartment(dept.get());
        } else {
            employee.setDepartment(null);
        }

        // Validate and resolve Designation FK
        if (updatedEmployee.getDesignation() != null
                && updatedEmployee.getDesignation().getId() != null) {
            Optional<Designation> desig = designationRepository.findById(
                    updatedEmployee.getDesignation().getId());
            if (desig.isEmpty()) {
                return response.send("Designation not found with id: "
                        + updatedEmployee.getDesignation().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setDesignation(desig.get());
        } else {
            employee.setDesignation(null);
        }

        // Validate and resolve Branch FK
        if (updatedEmployee.getBranch() != null
                && updatedEmployee.getBranch().getId() != null) {
            Optional<Branch> branch = branchRepository.findById(
                    updatedEmployee.getBranch().getId());
            if (branch.isEmpty()) {
                return response.send("Branch not found with id: "
                        + updatedEmployee.getBranch().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setBranch(branch.get());
        } else {
            employee.setBranch(null);
        }

        // Validate and resolve Manager FK — guard against self-assignment
        if (updatedEmployee.getManager() != null
                && updatedEmployee.getManager().getId() != null) {
            if (updatedEmployee.getManager().getId().equals(id)) {
                return response.send("An employee cannot be their own manager",
                        null, HttpStatus.BAD_REQUEST);
            }
            Optional<Employee> manager = employeeRepository.findById(
                    updatedEmployee.getManager().getId());
            if (manager.isEmpty()) {
                return response.send("Manager (Employee) not found with id: "
                        + updatedEmployee.getManager().getId(), null, HttpStatus.NOT_FOUND);
            }
            employee.setManager(manager.get());
        } else {
            employee.setManager(null);
        }

        Employee saved = employeeRepository.save(employee);
        return response.send("Employee updated successfully", saved, HttpStatus.OK);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> deleteEmployee(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return response.send("Employee not found with id: " + id, null, HttpStatus.NOT_FOUND);
        }
        employeeRepository.deleteById(id);
        return response.send("Employee deleted successfully", null, HttpStatus.OK);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    public ResponseEntity<ResponseWrapper> filterEmployees(String search,
                                                            Long departmentId,
                                                            Long designationId,
                                                            Long branchId,
                                                            String status,
                                                            String gender,
                                                            Long managerId,
                                                            String sortBy,
                                                            String sortDirection) {
        Specification<Employee> spec = Specification
                .where(EmployeeSpecification.searchByNameOrEmailOrCode(search))
                .and(EmployeeSpecification.hasDepartment(departmentId))
                .and(EmployeeSpecification.hasDesignation(designationId))
                .and(EmployeeSpecification.hasBranch(branchId))
                .and(EmployeeSpecification.hasStatus(status))
                .and(EmployeeSpecification.hasGender(gender))
                .and(EmployeeSpecification.hasManager(managerId))
                .and(EmployeeSpecification.sortByField(sortBy, sortDirection));

        List<Employee> employees = employeeRepository.findAll(spec);
        return response.send("Employees filtered successfully", employees, HttpStatus.OK);
    }

}
