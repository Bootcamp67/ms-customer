package com.bootcamp67.ms_customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreatedEvent {

  private String customerId;
  private String dni;
  private String fullName;
  private String email;
  private String phone;
  private String customerType;
  private String tier;
}
