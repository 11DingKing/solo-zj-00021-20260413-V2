package com.example.employeemanagement.model;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private Long employeeId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuditAction action;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  private String ipAddress;

  private String userAgent;

  @Column(length = 500)
  private String details;
}
