package com.example.employeemanagement.service;

import com.example.employeemanagement.model.AuditAction;
import com.example.employeemanagement.model.AuditLog;
import com.example.employeemanagement.model.User;
import com.example.employeemanagement.repository.AuditLogRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

  @Autowired private AuditLogRepository auditLogRepository;

  @Transactional
  public void logViewDetails(User user, Long employeeId, String ipAddress, String userAgent) {
    AuditLog auditLog = new AuditLog();
    auditLog.setUserId(user.getId());
    auditLog.setUsername(user.getUsername());
    auditLog.setEmployeeId(employeeId);
    auditLog.setAction(AuditAction.VIEW_DETAILS);
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setIpAddress(ipAddress);
    auditLog.setUserAgent(userAgent);
    auditLog.setDetails("Viewed employee details");
    auditLogRepository.save(auditLog);
  }
}
