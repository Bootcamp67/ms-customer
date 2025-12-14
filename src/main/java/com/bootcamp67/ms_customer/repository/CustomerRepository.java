package com.bootcamp67.ms_customer.repository;

import com.bootcamp67.ms_customer.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * The interface Customer repository.
 */
public interface CustomerRepository extends ReactiveMongoRepository<Customer,String> {
  /**
   * Find by dni mono.
   *
   * @param dni the dni
   * @return the mono
   */
  Mono<Customer> findByDni(String dni);
}
