package com.example.employeemanagement.util;

import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.model.User;
import com.example.employeemanagement.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

  @Autowired private UserRepository userRepository;

  public Optional<String> getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return Optional.empty();
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof org.springframework.security.core.userdetails.User) {
      return Optional.of(((org.springframework.security.core.userdetails.User) principal).getUsername());
    }
    if (principal instanceof String) {
      return Optional.of((String) principal);
    }
    return Optional.empty();
  }

  public Optional<User> getCurrentUser() {
    return getCurrentUsername().flatMap(userRepository::findByUsername);
  }

  public boolean isCurrentUserHr() {
    return getCurrentUser().map(user -> Role.HR.equals(user.getRole())).orElse(false);
  }

  public boolean isCurrentUserAdmin() {
    return getCurrentUser().map(user -> Role.ADMIN.equals(user.getRole())).orElse(false);
  }

  public boolean hasRole(Role role) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    String expectedAuthority = "ROLE_" + role.name();
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(auth -> auth.equals(expectedAuthority));
  }
}
