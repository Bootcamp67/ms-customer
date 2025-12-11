package com.bootcamp67.ms_customer.service;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
  Flux<CustomerDTO> findAll();
  Mono<CustomerDTO> findById(String id);
  Mono<CustomerDTO> create(CustomerRequest request);
  Mono<CustomerDTO> update(String id, CustomerRequest request);
  Mono<Void> delete(String id);
}
