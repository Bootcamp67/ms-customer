package com.bootcamp67.ms_customer.service;

import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.entity.Customer;
import com.bootcamp67.ms_customer.exception.NotFoundException;
import com.bootcamp67.ms_customer.repository.CustomerRepository;
import com.bootcamp67.ms_customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Reactive Tests")
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerServiceImpl customerService;

  private Customer customer;
  @MockBean
  private CustomerRequest customerRequest;

  @BeforeEach
  void setUp() {
    customer = Customer.builder()
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
    Customer customer2 = Customer.builder()
        .id("456")
        .dni("87654321")
        .fullName("Maria Garcia")
        .build();

    when(customerRepository.findAll()).thenReturn(Flux.just(customer, customer2));

    StepVerifier.create(customerService.findAll())
        .assertNext(dto -> assertThat(dto.getId()).isEqualTo("123"))
        .assertNext(dto -> assertThat(dto.getId()).isEqualTo("456"))
        .verifyComplete();

    verify(customerRepository, times(1)).findAll();
  }

  @Test
  @DisplayName("findAll - Should emit empty flux when no customers")
  void findAll_Empty() {
    when(customerRepository.findAll()).thenReturn(Flux.empty());

    StepVerifier.create(customerService.findAll())
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById - Should emit customer reactively")
  void findById_Success() {
    when(customerRepository.findById("123")).thenReturn(Mono.just(customer));

    StepVerifier.create(customerService.findById("123"))
        .assertNext(dto -> {
          assertThat(dto.getId()).isEqualTo("123");
          assertThat(dto.getDni()).isEqualTo("12345678");
          assertThat(dto.getFullName()).isEqualTo("Juan Perez");
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("findById - Should emit error when not found")
  void findById_NotFound() {
    when(customerRepository.findById(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(customerService.findById("999"))
        .expectError(NotFoundException.class)
        .verify();
  }

  @Test
  @DisplayName("create - Should emit created customer reactively")
  void create_Success() {
    when(customerRepository.findByDni(anyString())).thenReturn(Mono.empty());
    when(customerRepository.save(any(Customer.class)))
        .thenReturn(Mono.just(customer));

    StepVerifier.create(customerService.create(customerRequest))
        .assertNext(dto -> {
          assertThat(dto.getDni()).isEqualTo("12345678");
          assertThat(dto.getFullName()).isEqualTo("Juan Perez");
        })
        .verifyComplete();

    verify(customerRepository,times(1)).findByDni("12345678");
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  @DisplayName("update - Should emit updated customer reactively")
  void update_Success() {
    CustomerRequest updateRequest = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Juan Perez")
        .phone("98745621")
        .email("juan.perez@example.com")
        .build();

    Customer updatedCustomer = Customer.builder()
        .id("123")
        .dni("99999999")
        .fullName("Updated Name")
        .phone("999999999")
        .email("updated@example.com")
        .build();

    when(customerRepository.findById("123")).thenReturn(Mono.just(customer));
    when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(updatedCustomer));

    StepVerifier.create(customerService.update("123", updateRequest))
        .assertNext(dto -> {
          assertThat(dto.getDni()).isEqualTo("99999999");
          assertThat(dto.getFullName()).isEqualTo("Updated Name");
        })
        .verifyComplete();

    verify(customerRepository, times(1)).findById("123");
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  @DisplayName("update - Should emit error when customer not found")
  void update_NotFound() {
    when(customerRepository.findById("999")).thenReturn(Mono.empty());

    StepVerifier.create(customerService.update("999", customerRequest))
        .expectErrorMatches(throwable ->
            throwable instanceof NotFoundException &&
                throwable.getMessage().equals("Customer not found"))
        .verify();

    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  @DisplayName("delete - Should complete reactively")
  void delete_Success() {
    when(customerRepository.deleteById("123")).thenReturn(Mono.empty());

    StepVerifier.create(customerService.delete("123"))
        .verifyComplete();

    verify(customerRepository, times(1)).deleteById("123");
  }

  @Test
  @DisplayName("delete - Should propagate error reactively")
  void delete_Error() {
    when(customerRepository.deleteById("123"))
        .thenReturn(Mono.error(new RuntimeException("Database error")));

    StepVerifier.create(customerService.delete("123"))
        .expectError(RuntimeException.class)
        .verify();
  }

  @Test
  @DisplayName("findAll - Should handle repository error")
  void findAll_RepositoryError() {
    when(customerRepository.findAll())
        .thenReturn(Flux.error(new RuntimeException("Database connection failed")));

    StepVerifier.create(customerService.findAll())
        .expectError(RuntimeException.class)
        .verify();
  }

  @Test
  @DisplayName("create - Should handle repository error")
  void create_RepositoryError() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Arturo Mory")
            .build();

    when(customerRepository.findByDni("12345678"))
        .thenReturn(Mono.empty());

    when(customerRepository.save(any()))
        .thenReturn(Mono.error(new RuntimeException("DB error")));

    StepVerifier.create(customerService.create(request))
        .expectErrorMatches(err ->
            err instanceof RuntimeException &&
                err.getMessage().equals("DB error"))
        .verify();

    verify(customerRepository).findByDni("12345678");
    verify(customerRepository).save(any());
  }
}
