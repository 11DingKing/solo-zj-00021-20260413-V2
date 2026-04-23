package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.specification.EmployeeSpecification;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** This class represents the service for employees. */
@Service
public class EmployeeService {

  /** The employee repository. */
  @Autowired private EmployeeRepository employeeRepository;

  /** The JPA entity manager, used to flush and clear the persistence context after save. */
  @Autowired private EntityManager entityManager;

  /**
   * Get all employees.
   *
   * @return List of all employees
   */
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAllWithDepartments();
  }

  /**
   * Get employees with pagination and search.
   *
   * @param keyword Search keyword for firstName, lastName, email
   * @param pageable Pageable object for pagination
   * @return Page of employees
   */
  public Page<Employee> getEmployeesWithPagination(String keyword, Pageable pageable) {
    Specification<Employee> spec = EmployeeSpecification.searchByKeyword(keyword);
    return employeeRepository.findAll(spec, pageable);
  }

  /**
   * Get employee by ID.
   *
   * @param id ID of the employee to be retrieved
   * @return Employee with the specified ID
   */
  public Optional<Employee> getEmployeeById(Long id) {
    return employeeRepository.findByIdWithDepartment(id);
  }

  /**
   * Save an employee.
   *
   * @param employee Employee to be saved
   * @return Saved employee
   */
  @Transactional
  public Employee saveEmployee(Employee employee) {
    Employee saved = employeeRepository.save(employee);
    entityManager.flush();
    entityManager.clear();
    return employeeRepository.findByIdWithDepartment(saved.getId()).orElse(saved);
  }

  /**
   * Deletes an employee by their ID.
   *
   * @param id the ID of the employee to delete
   */
  public void deleteEmployee(Long id) {
    employeeRepository.deleteById(id);
  }
}
