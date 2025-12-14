package com.bootcamp67.ms_customer.controller;

import com.bootcamp67.ms_customer.config.TestMongoConfig;
import com.bootcamp67.ms_customer.dto.CustomerDTO;
import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.dto.CustomerResponse;
import com.bootcamp67.ms_customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CustomerController.class)
@TestPropertySource(properties = "spring.cloud.kubernetes.enabled=false")
@Import(TestMongoConfig.class)
class CustomerControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private CustomerService customerService;

  @Test
  void findAll_shouldReturnAllCustomers() {
    CustomerDTO customer1 = new CustomerDTO("1", "12345678", "Arturo Mory", null, null);
    CustomerDTO customer2 = new CustomerDTO("2", "87654321", "Maria Perez", null, null);

    when(customerService.findAll()).thenReturn(Flux.just(customer1, customer2));

    webTestClient.get()
        .uri("/api/v1/customers")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(CustomerDTO.class)
        .hasSize(2)
        .contains(customer1, customer2);
  }

  @Test
  void findById_shouldReturnCustomer() {
    CustomerDTO customer = new CustomerDTO("1", "12345678", "Arturo Mory", null, null);

    when(customerService.findById("1")).thenReturn(Mono.just(customer));

    webTestClient.get()
        .uri("/api/v1/customers/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(CustomerDTO.class)
        .isEqualTo(customer);
  }

  @Test
  void create_shouldReturnCreatedResponse() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Arturo Mory")
        .build();
    request.setDni("12345678");
    request.setFullName("Arturo Mory");

    CustomerDTO dto = new CustomerDTO("1", "12345678", "Arturo Mory", null, null);

    when(customerService.create(request)).thenReturn(Mono.just(dto));

    webTestClient.post()
        .uri("/api/v1/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CustomerResponse.class)
        .value(response -> {
          assert response.getMessage().equals("Created");
          assert response.getData().equals(dto);
        });
  }

  @Test
  void update_shouldReturnUpdatedCustomer() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Arturo Mory Updated")
        .build();
    CustomerDTO updated = new CustomerDTO("1", "12345678", "Arturo Mory Updated", null, null);

    when(customerService.update("1", request)).thenReturn(Mono.just(updated));

    webTestClient.put()
        .uri("/api/v1/customers/1")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(CustomerDTO.class)
        .isEqualTo(updated);
  }

  @Test
  void delete_shouldReturnVoid() {
    when(customerService.delete("1")).thenReturn(Mono.empty());

    webTestClient.delete()
        .uri("/api/v1/customers/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody().isEmpty();
  }
}
