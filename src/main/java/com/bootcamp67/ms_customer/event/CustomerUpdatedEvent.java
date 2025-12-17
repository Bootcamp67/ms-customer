package com.bootcamp67.ms_customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdatedEvent {

  private String customerId;
  private Map<String, Object> updatedFields;  // Changed fields
  private String updatedBy;
  private String reason;
}
