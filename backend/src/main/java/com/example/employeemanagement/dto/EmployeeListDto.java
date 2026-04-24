package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.EmployeeStatus;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeListDto {

  private Long id;

  private String firstName;

  private String lastName;

  private String email;

  private DepartmentDto department;

  private String position;

  private LocalDate hireDate;

  private EmployeeStatus status;

  @Data
  @NoArgsConstructor
  public static class DepartmentDto {
    private Long id;
    private String name;
  }
}
