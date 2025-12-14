package com.bootcamp67.ms_customer.service;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The interface Customer service.
 */
public interface CustomerService {
  /**
   * Find all flux.
   *
   * @return the flux
   */
  Flux<CustomerDTO> findAll();

  /**
   * Find by id mono.
   *
   * @param id the id
   * @return the mono
   */
  Mono<CustomerDTO> findById(String id);

  /**
   * Create mono.
   *
   * @param request the request
   * @return the mono
   */
  Mono<CustomerDTO> create(CustomerRequest request);

  /**
   * Update mono.
   *
   * @param id      the id
   * @param request the request
   * @return the mono
   */
  Mono<CustomerDTO> update(String id, CustomerRequest request);

  /**
   * Delete mono.
   *
   * @param id the id
   * @return the mono
   */
  Mono<Void> delete(String id);
}
