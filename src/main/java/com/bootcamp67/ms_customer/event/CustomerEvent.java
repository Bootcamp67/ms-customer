package com.bootcamp67.ms_customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {

  private String eventId;
  private String eventType;
  private String customerId;
  private LocalDateTime timestamp;
  private Object payload;

  public static class EventType {
    public static final String CUSTOMER_CREATED = "CUSTOMER_CREATED";
    public static final String CUSTOMER_UPDATED = "CUSTOMER_UPDATED";
    public static final String CUSTOMER_DELETED = "CUSTOMER_DELETED";
    public static final String CUSTOMER_BLOCKED = "CUSTOMER_BLOCKED";
    public static final String CUSTOMER_ACTIVATED = "CUSTOMER_ACTIVATED";
    public static final String CUSTOMER_UPGRADED = "CUSTOMER_UPGRADED";
    public static final String CUSTOMER_KYC_VERIFIED = "CUSTOMER_KYC_VERIFIED";
  }
}
