package com.example.employeemanagement.config;

import com.example.employeemanagement.model.Department;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.EmployeeStatus;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer implements CommandLineRunner {

  @Autowired private DepartmentRepository departmentRepository;

  @Autowired private EmployeeRepository employeeRepository;

  private final Faker faker = new Faker();

  private final Random random = new Random();

  private static final String[] POSITIONS = {
    "Software Engineer",
    "Senior Software Engineer",
    "Tech Lead",
    "Product Manager",
    "QA Engineer",
    "UX Designer",
    "Data Analyst",
    "HR Manager",
    "Accountant",
    "Marketing Specialist",
    "Sales Representative",
    "Office Manager"
  };

  @Override
  public void run(String... args) {
    if (departmentRepository.count() > 0) {
      System.out.println("Data already exists, skipping initialization.");
      return;
    }

    List<Department> departments = new ArrayList<>();
    for (int i = 1; i <= 50; i++) {
      Department department = new Department();
      department.setName(faker.company().industry());
      departments.add(department);
    }
    departmentRepository.saveAll(departments);

    List<Employee> employees = new ArrayList<>();
    for (int i = 1; i <= 295; i++) {
      Employee employee = new Employee();
      employee.setFirstName(faker.name().firstName());
      employee.setLastName(faker.name().lastName());
      employee.setEmail(faker.internet().emailAddress());
      employee.setAge(random.nextInt(40) + 20);
      employee.setIdCard(faker.idNumber().ssnValid());
      employee.setSalary(BigDecimal.valueOf(faker.number().numberBetween(50000, 200000)));
      employee.setPosition(POSITIONS[random.nextInt(POSITIONS.length)]);

      Date hireDate = faker.date().past(365 * 10, TimeUnit.DAYS);
      employee.setHireDate(hireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

      EmployeeStatus[] statuses = EmployeeStatus.values();
      employee.setStatus(statuses[random.nextInt(3)]);

      employee.setDepartment(departments.get(random.nextInt(departments.size())));
      employees.add(employee);
    }
    employeeRepository.saveAll(employees);

    System.out.println("Fake data initialized successfully, replacing any existing data!");
  }
}
