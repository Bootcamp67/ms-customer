package com.bootcamp67.ms_customer.service.impl;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.entity.Customer;
import com.bootcamp67.ms_customer.exception.NotFoundException;
import com.bootcamp67.ms_customer.repository.CustomerRepository;
import com.bootcamp67.ms_customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;

  @Override
  public Flux<CustomerDTO> findAll() {
    return customerRepository.findAll()
        .map(this::mapToDto);
  }

  @Override
  public Mono<CustomerDTO> findById(String id) {
    return customerRepository.findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
        .map(this::mapToDto);
  }

  @Override
  public Mono<CustomerDTO> create(CustomerRequest request) {
    Customer customer=Customer.builder()
        .dni(request.getDni())
        .fullName(request.getFullName())
        .phone(request.getPhone())
        .email(request.getEmail())
        .build();
    return customerRepository.save(customer).map(this::mapToDto);
  }

  @Override
  public Mono<CustomerDTO> update(String id, CustomerRequest request) {
    return customerRepository.findById(id)
        .switchIfEmpty(Mono.error(new NotFoundException("Customer not found")))
        .flatMap(c->{
          c.setDni(request.getDni());
          c.setFullName(request.getFullName());
          c.setPhone(request.getPhone());
          c.setEmail(request.getEmail());
          return customerRepository.save(c);
        })
        .map(this::mapToDto);
  }

  @Override
  public Mono<Void> delete(String id) {
    return customerRepository.deleteById(id);
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
