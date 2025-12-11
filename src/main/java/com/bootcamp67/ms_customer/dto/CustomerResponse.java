package com.bootcamp67.ms_customer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
  private String message;
  private CustomerDTO data;
}
