package com.bootcamp67.ms_customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerRequest {
  @NotBlank
  private String dni;
  @NotBlank
  private String fullName;
  private String phone;
  private String email;
}
