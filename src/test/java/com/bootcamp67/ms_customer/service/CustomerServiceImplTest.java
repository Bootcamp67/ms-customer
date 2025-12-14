package com.bootcamp67.ms_customer.service;

import com.bootcamp67.ms_customer.dto.CustomerRequest;
import com.bootcamp67.ms_customer.entity.Customer;
import com.bootcamp67.ms_customer.exception.NotFoundException;
import com.bootcamp67.ms_customer.repository.CustomerRepository;
import com.bootcamp67.ms_customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomerServiceImplTest {

  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerServiceImpl service;

  @Test
  void findAll_shouldReturnListOfCustomers() {
    Customer c1 = new Customer("1", "123", "Alice", "111", "a@mail.com");
    Customer c2 = new Customer("2", "456", "Bob", "222", "b@mail.com");

    when(customerRepository.findAll()).thenReturn(Flux.just(c1, c2));

    StepVerifier.create(service.findAll())
        .expectNextMatches(dto -> dto.getFullName().equals("Alice"))
        .expectNextMatches(dto -> dto.getFullName().equals("Bob"))
        .verifyComplete();

    verify(customerRepository, times(1)).findAll();
  }

  @Test
  void findById_whenCustomerExists_shouldReturnCustomer() {
    Customer c = new Customer("1", "123", "Alice", "111", "a@mail.com");
    when(customerRepository.findById("1")).thenReturn(Mono.just(c));

    StepVerifier.create(service.findById("1"))
        .expectNextMatches(dto -> dto.getFullName().equals("Alice"))
        .verifyComplete();

    verify(customerRepository, times(1)).findById("1");
  }

  @Test
  void findById_whenCustomerNotFound_shouldThrowNotFoundException() {
    when(customerRepository.findById("1")).thenReturn(Mono.empty());

    StepVerifier.create(service.findById("1"))
        .expectErrorMatches(err -> err instanceof NotFoundException &&
            err.getMessage().equals("Customer not found"))
        .verify();

    verify(customerRepository, times(1)).findById("1");
  }

  @Test
  void create_whenDocumentNotExists_shouldCreateCustomer() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("87654321")
        .fullName("Bob")
        .build();

    Customer savedCustomer = Customer.builder()
        .id("1")
        .dni("87654321")
        .fullName("Bob")
        .build();

    // Documento no existe
    when(customerRepository.findByDni("87654321")).thenReturn(Mono.empty());
    when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(savedCustomer));

    StepVerifier.create(service.create(request))
        .expectNextMatches(dto -> dto.getId().equals("1") && dto.getFullName().equals("Bob"))
        .verifyComplete();
  }
  @Test
  void create_whenDocumentNotExists_shouldSaveCustomer() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("123")
        .fullName("Alice")
        .phone(null)
        .email(null)
        .build();
    Customer saved = new Customer("1", "123", "Alice", null, null);

    when(customerRepository.findByDni("123")).thenReturn(Mono.empty());
    when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(saved));

    StepVerifier.create(service.create(request))
        .expectNextMatches(dto -> dto.getId().equals("1") && dto.getFullName().equals("Alice"))
        .verifyComplete();

    verify(customerRepository, times(1)).findByDni("123");
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void update_whenCustomerExists_shouldReturnUpdatedCustomer() {
    Customer existing = new Customer("1", "123", "Alice", "111", "a@mail.com");
    CustomerRequest request = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Arturo Update")
        .phone("995937466")
        .email("morylex@gmail.com")
        .build();
    Customer updated = new Customer("1", "999", "Alice Updated", "999", "new@mail.com");

    when(customerRepository.findById("1")).thenReturn(Mono.just(existing));
    when(customerRepository.save(existing)).thenReturn(Mono.just(updated));

    StepVerifier.create(service.update("1", request))
        .expectNextMatches(dto -> dto.getDni().equals("999") && dto.getFullName().equals("Alice Updated"))
        .verifyComplete();

    verify(customerRepository, times(1)).findById("1");
    verify(customerRepository, times(1)).save(existing);
  }

  @Test
  void update_whenCustomerNotFound_shouldThrowNotFoundException() {
    CustomerRequest request = CustomerRequest.builder()
        .dni("12345678")
        .fullName("Arturo Update")
        .phone("995937466")
        .email("morylex@gmail.com")
        .build();

    when(customerRepository.findById("1")).thenReturn(Mono.empty());

    StepVerifier.create(service.update("1", request))
        .expectErrorMatches(err -> err instanceof NotFoundException &&
            err.getMessage().equals("Customer not found"))
        .verify();

    verify(customerRepository, times(1)).findById("1");
    verify(customerRepository, never()).save(any());
  }

  @Test
  void delete_shouldCallRepositoryDelete() {
    when(customerRepository.deleteById("1")).thenReturn(Mono.empty());

    StepVerifier.create(service.delete("1"))
        .verifyComplete();

    verify(customerRepository, times(1)).deleteById("1");
  }
}
