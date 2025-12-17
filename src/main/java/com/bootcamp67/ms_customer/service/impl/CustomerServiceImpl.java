package com.bootcamp67.ms_customer.service.impl;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.entity.Customer;
import com.bootcamp67.ms_customer.event.CustomerCreatedEvent;
import com.bootcamp67.ms_customer.event.CustomerUpdatedEvent;
import com.bootcamp67.ms_customer.event.producer.CustomerEventProducer;
import com.bootcamp67.ms_customer.exception.NotFoundException;
import com.bootcamp67.ms_customer.repository.CustomerRepository;
import com.bootcamp67.ms_customer.service.CustomerService;
import com.bootcamp67.ms_customer.service.cache.CustomerCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Customer service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerCacheService customerCacheService;
  private final CustomerEventProducer customerEventProducer;

  @Override
  public Flux<CustomerDTO> findAll() {
    return customerRepository.findAll()
        .map(this::mapToDto);
  }

  @Override
  public Mono<CustomerDTO> findById(String id) {
    log.info("Finding customer by id: {}", id);
    return customerCacheService.getConsumer(id)
        .switchIfEmpty(
            customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
                .map(this::mapToDto)
                .flatMap(dto->
                  customerCacheService.saveCustomer(dto)
                      .thenReturn(dto)
                )
        )
        .doOnSuccess(dto->log.info("Customer retrieved: {}",dto.getId()));
  }

  @Override
  public Mono<CustomerDTO> create(CustomerRequest request) {
    return customerRepository.findByDni(request.getDni())
        .flatMap(existing -> Mono.<CustomerDTO>error(new IllegalArgumentException("Document exists")))
        .switchIfEmpty(
            customerRepository.save(
                Customer.builder()
                    .dni(request.getDni())
                    .fullName(request.getFullName())
                    .build()
            )
                .flatMap(savedCustomer->{
                  CustomerDTO dto = mapToDto(savedCustomer);
                  CustomerCreatedEvent customerCreatedEvent=CustomerCreatedEvent.builder()
                      .customerId(savedCustomer.getId())
                      .dni(savedCustomer.getDni())
                      .fullName(savedCustomer.getFullName())
                      .email(savedCustomer.getEmail())
                      .phone(savedCustomer.getPhone())
                      .tier("BASIC")
                      .build();
                  return Mono.zip(
                      customerCacheService.saveCustomer(dto),
                      customerEventProducer.publishCustomerCreated(customerCreatedEvent)
                  ).thenReturn(dto)
                      .doOnSuccess(dtoSuccess->log.info("Customer created even published for: {}",dtoSuccess.getId()))
                      .doOnError(error->log.info("Error publishing customer created event: {}",error.getMessage()));
                })
        );
  }

  @Override
  public Mono<CustomerDTO> update(String id, CustomerRequest request) {
    log.info("Updating customer: {}", id);
    return customerRepository.findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
        .flatMap(c->{
          Map<String, Object> changedFields = new HashMap<>();

          if (!c.getDni().equals(request.getDni())) {
            changedFields.put("dni", request.getDni());
            c.setDni(request.getDni());
          }
          if (!c.getFullName().equals(request.getFullName())) {
            changedFields.put("fullName", request.getFullName());
            c.setFullName(request.getFullName());
          }
          if (!c.getPhone().equals(request.getPhone())) {
            changedFields.put("phone", request.getPhone());
            c.setPhone(request.getPhone());
          }
          if (!c.getEmail().equals(request.getEmail())) {
            changedFields.put("email", request.getEmail());
            c.setEmail(request.getEmail());
          }          c.setDni(request.getDni());
          c.setFullName(request.getFullName());
          c.setPhone(request.getPhone());
          c.setEmail(request.getEmail());
          return customerRepository.save(c)
              .flatMap(updatedCustomer->{
                log.info("Customer updated: {}", updatedCustomer.getId());

                CustomerDTO dto = mapToDto(updatedCustomer);

                CustomerUpdatedEvent event = CustomerUpdatedEvent.builder()
                    .customerId(updatedCustomer.getId())
                    .updatedFields(changedFields)
                    .updatedAt(LocalDateTime.now())
                    .build();

                return Mono.zip(
                        customerCacheService.saveCustomer(dto),
                        customerEventProducer.publishCustomerUpdated(event)
                    )
                    .thenReturn(dto)
                    .doOnSuccess(d -> log.info("Customer updated, cache refreshed and event published: {}", d.getId()))
                    .doOnError(error -> log.error("Error in update operation: {}", error.getMessage()));
              });
        });
  }

  @Override
  public Mono<Void> delete(String id) {
    log.info("Deleting customer: {}", id);
    return customerRepository.findByDni(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Customer Not Found")))
            .flatMap(customer ->
                Mono.zip(
                    customerRepository.deleteById(id),
                    customerCacheService.deleteCustomer(id),
                    customerEventProducer.publishCustomerDeleted(customer.getId(),
                        "Deleting Customer ID",customer.getFullName())
                )
                    .then()
                    .doOnSuccess(v->log.info("Customer deleted, cache invalidated and event published: {}", id))
                    .doOnError(error->log.error("Error in delete operation: {}", error.getMessage()))
            );
  }

  private CustomerDTO mapToDto(Customer customer){
    return CustomerDTO.builder()
        .id(customer.getId())
        .dni(customer.getDni())
        .fullName(customer.getFullName())
        .phone(customer.getPhone())
        .email(customer.getEmail())
        .build();
  }
}
