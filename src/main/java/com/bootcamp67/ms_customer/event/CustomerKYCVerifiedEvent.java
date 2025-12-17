package com.bootcamp67.ms_customer.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerKYCVerifiedEvent {

  private String customerId;
  private String verificationLevel;
  private String verifiedBy;
  private String verificationMethod;
  private Boolean documentVerified;
  private Boolean addressVerified;
  private Boolean identityVerified;

}
