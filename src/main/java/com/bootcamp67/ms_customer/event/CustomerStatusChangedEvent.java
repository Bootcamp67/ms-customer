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
public class CustomerStatusChangedEvent {

  private String customerId;
  private String previousStatus;
  private String newStatus;
  private String reason;
  private String requestedBy;
  private LocalDateTime changedAt;
  private String ipAddress;
}
