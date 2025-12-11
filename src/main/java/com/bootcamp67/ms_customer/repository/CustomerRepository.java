package com.bootcamp67.ms_customer.repository;

import com.bootcamp67.ms_customer.entity.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer,String> {
  Mono<Customer> findByDni(String dni);
}
