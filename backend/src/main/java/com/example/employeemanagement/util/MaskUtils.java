package com.example.employeemanagement.util;

import java.math.BigDecimal;

public class MaskUtils {

  private MaskUtils() {
  }

  public static String maskIdCard(String idCard) {
    if (idCard == null || idCard.length() < 8) {
      return idCard;
    }
    int length = idCard.length();
    StringBuilder masked = new StringBuilder();
    for (int i = 0; i < length - 4; i++) {
      masked.append('*');
    }
    masked.append(idCard.substring(length - 4));
    return masked.toString();
  }

  public static BigDecimal maskSalary(BigDecimal salary) {
    if (salary == null) {
      return null;
    }
    return BigDecimal.valueOf(0);
  }

  public static String maskSalaryAsString(BigDecimal salary) {
    if (salary == null) {
      return null;
    }
    return "****";
  }
}
