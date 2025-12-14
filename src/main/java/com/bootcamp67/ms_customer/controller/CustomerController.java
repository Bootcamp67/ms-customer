package com.bootcamp67.ms_customer.controller;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.dto.CustomerResponse;
import com.bootcamp67.ms_customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * The type Customer controller.
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  /**
   * Find all flux.
   *
   * @return the flux
   */
  @GetMapping
  public Flux<CustomerDTO> findAll(){
    return customerService.findAll();
  }

  /**
   * Find by id mono.
   *
   * @param id the id
   * @return the mono
   */
  @GetMapping("/{id}")
  public Mono<CustomerDTO> findById(@PathVariable String id){
    return customerService.findById(id);
  }

  /**
   * Create mono.
   *
   * @param customerRequest the customer request
   * @return the mono
   */
  @PostMapping
  public Mono<ResponseEntity<CustomerResponse>> create(
      @RequestBody
      @Valid CustomerRequest customerRequest){
    return customerService.create(customerRequest)
        .map(dto->ResponseEntity.ok(
            CustomerResponse.builder()
                .message("Created")
                .data(dto)
                .build()
        ));
  }

  /**
   * Update mono.
   *
   * @param id              the id
   * @param customerRequest the customer request
   * @return the mono
   */
  @PutMapping("/{id}")
  public Mono<CustomerDTO> update(@PathVariable String id, @Valid @RequestBody CustomerRequest customerRequest) {
    return customerService.update(id, customerRequest);
  }

  /**
   * Delete mono.
   *
   * @param id the id
   * @return the mono
   */
  @DeleteMapping("/{id}")
  public Mono<Void> delete(@PathVariable String id){
    return customerService.delete(id);
  }
}
