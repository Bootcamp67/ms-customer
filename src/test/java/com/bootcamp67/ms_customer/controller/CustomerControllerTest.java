package com.bootcamp67.ms_customer.controller;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Controller Reactive Tests")
class CustomerControllerTest {

  @Mock
  private CustomerService customerService;

  @InjectMocks
  private CustomerController customerController;

  private CustomerDTO customerDTO;
  private CustomerRequest customerRequest;

  @BeforeEach
  void setUp() {
    customerDTO = CustomerDTO.builder()
        .id("123")
        .dni("12345678")
        .fullName("Juan Perez")
        .phone("987654321")
        .email("juan.perez@example.com")
        .build();

    customerRequest = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Juan Perez")
        .phone("98745621")
        .email("juan.perez@example.com")
        .build();
  }

  @Test
  @DisplayName("findAll - Should emit all customers reactively")
  void findAll_Success() {
    CustomerDTO customer2 = CustomerDTO.builder()
        .id("456")
        .dni("87654321")
        .fullName("Maria Garcia")
        .build();

    when(customerService.findAll()).thenReturn(Flux.just(customerDTO, customer2));

    StepVerifier.create(customerController.findAll())
        .expectNext(customerDTO)
        .expectNext(customer2)
        .verifyComplete();

    verify(customerService, times(1)).findAll();
  }

  @Test
  @DisplayName("findById - Should emit customer reactively")
  void findById_Success() {
    when(customerService.findById("123")).thenReturn(Mono.just(customerDTO));

    StepVerifier.create(customerController.findById("123"))
        .assertNext(c -> {
          assertThat(c.getId()).isEqualTo("123");
          assertThat(c.getDni()).isEqualTo("12345678");
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("create - Should emit response reactively")
  void create_Success() {
    when(customerService.create(any(CustomerRequest.class)))
        .thenReturn(Mono.just(customerDTO));

    StepVerifier.create(customerController.create(customerRequest))
        .assertNext(response -> {
          assertThat(response.getBody().getMessage()).isEqualTo("Created");
          assertThat(response.getBody().getData().getId()).isEqualTo("123");
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("delete - Should complete reactively")
  void delete_Success() {
    when(customerService.delete("123")).thenReturn(Mono.empty());

    StepVerifier.create(customerController.delete("123"))
        .verifyComplete();
  }
}