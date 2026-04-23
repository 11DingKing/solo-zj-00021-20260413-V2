package com.example.employeemanagement.specification;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

  public static Specification<Employee> searchByKeyword(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      String searchPattern = "%" + keyword.toLowerCase() + "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchPattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchPattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern));
    };
  }
}
