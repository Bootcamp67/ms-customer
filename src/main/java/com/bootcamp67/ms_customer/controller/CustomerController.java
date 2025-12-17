package com.bootcamp67.ms_customer.controller;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.dto.CustomerResponse;
import com.bootcamp67.ms_customer.service.CustomerService;
import com.bootcamp67.ms_customer.service.cache.CustomerCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

/**
 * The type Customer controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;
  private final CustomerCacheService customerCacheService;

  /**
   * Find all flux.
   *
   * @return the flux
   */
  @GetMapping
  @ResponseBody
  public Flux<CustomerDTO> findAll(){
    log.info("GET /api/v1/customers - Finding all customers");
    return customerService.findAll();
  }

  /**
   * Find by id mono.
   *
   * @param id the id
   * @return the mono
   */
  @GetMapping("/{id}")
  @ResponseBody
  public Mono<CustomerDTO> findById(@PathVariable String id) {
    log.info("GET /api/v1/customers/{} - Finding customer by id", id);
    return customerService.findById(id);
  }


  /**
   * Create mono.
   *
   * @param customerRequest the customer request
   * @return the mono
   */
  @PostMapping
  @ResponseBody
  public Mono<ResponseEntity<CustomerResponse>> create(
      @RequestBody
      @Valid CustomerRequest customerRequest){
    log.info("POST /api/v1/customers - Creating customer with DNI: {}", customerRequest.getDni());
    return customerService.create(customerRequest)
        .map(dto->ResponseEntity.ok(
            CustomerResponse.builder()
                .message("Created")
                .data(dto)
                .build()
        ))
        .doOnSuccess(response -> log.info("Customer created: {}", response.getBody().getData().getId()))
        .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
  }

  /**
   * Update mono.
   *
   * @param id              the id
   * @param customerRequest the customer request
   * @return the mono
   */
  @PutMapping("/{id}")
  @ResponseBody
  public Mono<ResponseEntity<CustomerResponse>> update(
      @PathVariable String id,
      @Valid
      @RequestBody CustomerRequest customerRequest) {
    log.info("PUT /api/v1/customers/{} - Updating customer", id);
    return customerService.update(id, customerRequest)
        .map(dto->
            ResponseEntity.ok(
                CustomerResponse.builder()
                    .message("Customer update successfully")
                    .data(dto)
                    .build()
            ))
        .doOnSuccess(response->log.info("Customer update: {}",id))
        .doOnError(error->log.error("Error updating customer {}: {}",id,error.getMessage()));
  }

  /**
   * Delete mono.
   *
   * @param id the id
   * @return the mono
   */
  @DeleteMapping("/{id}")
  @ResponseBody
  public Mono<ResponseEntity<Void>> delete(@PathVariable String id){
    log.info("DELETE /api/v1/customers/{} - Deleting customer", id);
    return customerService.delete(id)
        .then(Mono.just(ResponseEntity.noContent().<Void>build()))
        .doOnSuccess(response -> log.info("Customer deleted: {}", id))
        .doOnError(error -> log.error("Error deleting customer {}: {}", id, error.getMessage()));
  }


  @DeleteMapping("/{id}/cache")
  @ResponseBody
  public Mono<ResponseEntity<Map<String, String>>> invalidateCache(@PathVariable String id) {
    log.info("DELETE /api/v1/customers/{}/cache - Invalidating cache", id);

    return customerCacheService.deleteCustomer(id)
        .map(deleted -> {
          if (deleted) {
            return ResponseEntity.ok(Map.of(
                "message", "Cache invalidated successfully",
                "customerId", id
            ));
          } else {
            return ResponseEntity.ok(Map.of(
                "message", "Customer not found in cache",
                "customerId", id
            ));
          }
        });
  }

  @DeleteMapping("/cache")
  @ResponseBody
  public Mono<ResponseEntity<Map<String, String>>> clearAllCache() {
    log.info("DELETE /api/v1/customers/cache - Clearing all customer cache");

    return customerCacheService.clearAll()
        .then(Mono.just(ResponseEntity.ok(Map.of(
            "message", "All customer cache cleared successfully"
        ))));
  }

  @GetMapping("/{id}/cache/exists")
  @ResponseBody
  public Mono<ResponseEntity<Map<String, Object>>> checkCache(@PathVariable String id) {
    log.info("GET /api/v1/customers/{}/cache/exists - Checking cache", id);

    return customerCacheService.exists(id)
        .flatMap(exists -> {
          if (exists) {
            return customerCacheService.getTTL(id)
                .map(ttl -> ResponseEntity.ok(Map.<String, Object>of(
                    "cached", true,
                    "customerId", id,
                    "ttlSeconds", ttl.getSeconds()
                )));
          } else {
            return Mono.just(ResponseEntity.ok(Map.<String, Object>of(
                "cached", false,
                "customerId", id
            )));
          }
        });
  }
}
