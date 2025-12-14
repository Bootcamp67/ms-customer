package com.bootcamp67.ms_customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Customer dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
  private String id;
  private String dni;
  private String fullName;
  private String phone;
  private String email;
}
