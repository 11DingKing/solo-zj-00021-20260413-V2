package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.EmployeeStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeDetailDto {

  private Long id;

  private String firstName;

  private String lastName;

  private String email;

  private int age;

  private String idCard;

  private BigDecimal salary;

  private String position;

  private LocalDate hireDate;

  private EmployeeStatus status;

  private DepartmentDto department;

  @Data
  @NoArgsConstructor
  public static class DepartmentDto {
    private Long id;
    private String name;
  }
}
