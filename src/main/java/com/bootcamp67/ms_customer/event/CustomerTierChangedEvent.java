package com.bootcamp67.ms_customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTierChangedEvent {

  private String customerId;
  private String previousTier;
  private String newTier;
  private String reason;
  private String triggeredBy;
}
