package com.bootcamp67.ms_customer.dto;

import lombok.Builder;
import lombok.Data;

/**
 * The type Customer response.
 */
@Data
@Builder
public class CustomerResponse {
  private String message;
  private CustomerDTO data;
}
