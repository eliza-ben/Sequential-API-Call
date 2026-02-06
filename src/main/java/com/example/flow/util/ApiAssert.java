package com.example.flow.util;

public final class ApiAssert {
  private ApiAssert() {}

  public static void require200(int status, String step) {
    if (status != 200) throw new ApiFlowException(step + " failed: HTTP " + status);
  }

  public static void requireNotBlank(String v, String step, String field) {
    if (v == null || v.trim().isEmpty()) throw new ApiFlowException(step + " failed: " + field + " missing");
  }

  public static void requireNotNull(Object v, String step, String field) {
    if (v == null) throw new ApiFlowException(step + " failed: " + field + " missing");
  }

  public static void requireMessageTypeSuccess(String messageType, String expectedSuccess, String step) {
    if (messageType == null) throw new ApiFlowException(step + " failed: messageType is null");
    if (!messageType.trim().equalsIgnoreCase(expectedSuccess)) {
      throw new ApiFlowException(step + " failed: messageType=" + messageType + " (expected " + expectedSuccess + ")");
    }
  }

  // Step5 needs to allow PENDING (and sometimes SUCCESS)
  public static void requireMessageTypeIn(String messageType, String step, String... allowed) {
    if (messageType == null) throw new ApiFlowException(step + " failed: messageType is null");

    String mt = messageType.trim().toUpperCase();
    for (String a : allowed) {
      if (a != null && mt.equals(a.trim().toUpperCase())) return;
    }
    throw new ApiFlowException(step + " failed: messageType=" + messageType +
        " (allowed: " + String.join(",", allowed) + ")");
  }
}
