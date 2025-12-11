package com.bootcamp67.ms_customer.controller;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.dto.CustomerResponse;
import com.bootcamp67.ms_customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerContoller {

  private final CustomerService customerService;

  @GetMapping
  public Flux<CustomerDTO> findAll(){
    return customerService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<CustomerDTO> findById(@PathVariable String id){
    return customerService.findById(id);
  }

  @PostMapping
  public Mono<ResponseEntity<CustomerResponse>> create(@RequestBody @Valid CustomerRequest customerRequest){
    return customerService.create(customerRequest)
        .map(dto->ResponseEntity.ok(
            CustomerResponse.builder()
                .message("Created")
                .data(dto)
                .build()
        ));
  }

  @PutMapping("/{id}")
  public Mono<Void> delete(@PathVariable String id){
    return customerService.delete(id);
  }
}
