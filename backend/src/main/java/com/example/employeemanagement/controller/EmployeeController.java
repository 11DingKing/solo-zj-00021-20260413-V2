package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDetailDto;
import com.example.employeemanagement.dto.EmployeeListDto;
import com.example.employeemanagement.dto.EmployeeRequestDto;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.model.User;
import com.example.employeemanagement.service.AuditLogService;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.util.MaskUtils;
import com.example.employeemanagement.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Employees APIs", description = "API Operations related to managing employees")
public class EmployeeController {

  @Autowired private EmployeeService employeeService;

  @Autowired private DepartmentService departmentService;

  @Autowired private AuditLogService auditLogService;

  @Autowired private SecurityUtils securityUtils;

  @Operation(summary = "Get all employees with pagination", description = "Retrieve a paginated list of employees with optional search")
  @GetMapping
  public Map<String, Object> getAllEmployees(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
    Page<Employee> employeePage = employeeService.getEmployeesWithPagination(keyword, pageable);

    List<EmployeeListDto> content = employeePage.getContent().stream()
        .map(this::convertToListDto)
        .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("content", content);
    response.put("totalPages", employeePage.getTotalPages());
    response.put("totalElements", employeePage.getTotalElements());
    response.put("currentPage", employeePage.getNumber());
    response.put("size", employeePage.getSize());

    return response;
  }

  @Operation(
      summary = "Get employee by ID",
      description = "Retrieve a specific employee by their ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Employee found"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
      })
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDetailDto> getEmployeeById(
      @PathVariable Long id,
      HttpServletRequest request) {
    Employee employee =
        employeeService
            .getEmployeeById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

    Optional<User> currentUser = securityUtils.getCurrentUser();
    boolean isHr = currentUser.map(user -> Role.HR.equals(user.getRole())).orElse(false);

    currentUser.ifPresent(user -> {
      String ipAddress = getClientIpAddress(request);
      String userAgent = request.getHeader("User-Agent");
      auditLogService.logViewDetails(user, id, ipAddress, userAgent);
    });

    EmployeeDetailDto dto = convertToDetailDto(employee, isHr);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Create a new employee", description = "Create a new employee record")
  @PostMapping
  public ResponseEntity<EmployeeDetailDto> createEmployee(
      @Valid @RequestBody EmployeeRequestDto request) {
    Employee employee = convertToEntity(request);
    Employee savedEmployee = employeeService.saveEmployee(employee);
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
        .body(convertToDetailDto(savedEmployee, true));
  }

  @Operation(
      summary = "Update an existing employee",
      description = "Update an existing employee's details")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Employee updated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
      })
  @PutMapping("/{id}")
  public ResponseEntity<EmployeeDetailDto> updateEmployee(
      @PathVariable Long id, @Valid @RequestBody EmployeeRequestDto request) {
    Employee employee =
        employeeService
            .getEmployeeById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

    Department department =
        departmentService
            .getDepartmentById(request.getDepartment().getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartment().getId()));

    employee.setFirstName(request.getFirstName());
    employee.setLastName(request.getLastName());
    employee.setEmail(request.getEmail());
    employee.setDepartment(department);
    employee.setAge(request.getAge());

    Employee updatedEmployee = employeeService.saveEmployee(employee);
    return ResponseEntity.ok(convertToDetailDto(updatedEmployee, true));
  }

  @Operation(summary = "Delete an employee", description = "Delete an employee record by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    employeeService
        .getEmployeeById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  private EmployeeListDto convertToListDto(Employee employee) {
    EmployeeListDto dto = new EmployeeListDto();
    dto.setId(employee.getId());
    dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName());
    dto.setEmail(employee.getEmail());
    dto.setPosition(employee.getPosition());
    dto.setHireDate(employee.getHireDate());
    dto.setStatus(employee.getStatus());

    if (employee.getDepartment() != null) {
      EmployeeListDto.DepartmentDto deptDto = new EmployeeListDto.DepartmentDto();
      deptDto.setId(employee.getDepartment().getId());
      deptDto.setName(employee.getDepartment().getName());
      dto.setDepartment(deptDto);
    }
    return dto;
  }

  private EmployeeDetailDto convertToDetailDto(Employee employee, boolean isHr) {
    EmployeeDetailDto dto = new EmployeeDetailDto();
    dto.setId(employee.getId());
    dto.setFirstName(employee.getFirstName());
    dto.setLastName(employee.getLastName());
    dto.setEmail(employee.getEmail());
    dto.setAge(employee.getAge());
    dto.setPosition(employee.getPosition());
    dto.setHireDate(employee.getHireDate());
    dto.setStatus(employee.getStatus());

    if (isHr) {
      dto.setIdCard(employee.getIdCard());
      dto.setSalary(employee.getSalary());
    } else {
      dto.setIdCard(MaskUtils.maskIdCard(employee.getIdCard()));
      dto.setSalary(maskSalary(employee.getSalary()));
    }

    if (employee.getDepartment() != null) {
      EmployeeDetailDto.DepartmentDto deptDto = new EmployeeDetailDto.DepartmentDto();
      deptDto.setId(employee.getDepartment().getId());
      deptDto.setName(employee.getDepartment().getName());
      dto.setDepartment(deptDto);
    }
    return dto;
  }

  private BigDecimal maskSalary(BigDecimal salary) {
    if (salary == null) {
      return null;
    }
    return BigDecimal.ZERO;
  }

  private Employee convertToEntity(EmployeeRequestDto request) {
    Employee employee = new Employee();
    employee.setFirstName(request.getFirstName());
    employee.setLastName(request.getLastName());
    employee.setEmail(request.getEmail());
    employee.setAge(request.getAge());

    Department department =
        departmentService
            .getDepartmentById(request.getDepartment().getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartment().getId()));
    employee.setDepartment(department);

    return employee;
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip != null && ip.contains(",")) {
      ip = ip.split(",")[0].trim();
    }
    return ip;
  }
}
