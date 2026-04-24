package com.example.employeemanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @Min(value = 18, message = "Age must be at least 18")
  @Max(value = 65, message = "Age must be at most 65")
  private int age;

  @NotBlank(message = "ID card is required")
  private String idCard;

  private BigDecimal salary;

  private String position;

  private LocalDate hireDate;

  @Enumerated(EnumType.STRING)
  private EmployeeStatus status;

  @NotNull(message = "Department is required")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "department_id", nullable = false)
  @JsonBackReference
  private Department department;
}
