package com.bootcamp67.ms_customer.exception;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global Exception Handler Reactive Tests")

class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  @DisplayName("handleNotFound - Should emit 404 response reactively")
  void handleNotFound() {
    NotFoundException exception = new NotFoundException("Customer not found");

    StepVerifier.create(exceptionHandler.handleNotFound(exception))
        .assertNext(response -> {
          assertThat(response.getStatusCodeValue()).isEqualTo(404);
          assertThat(response.getBody()).isNotNull();
          assertThat(response.getBody().get("error")).isEqualTo("Customer not found");
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("handleGeneral - Should emit 500 response reactively")
  void handleGeneral() {
    Exception exception = new Exception("Internal server error");

    StepVerifier.create(exceptionHandler.handleGeneral(exception))
        .assertNext(response -> {
          assertThat(response.getStatusCodeValue()).isEqualTo(500);
          assertThat(response.getBody()).isNotNull();
          assertThat(response.getBody().get("error")).isEqualTo("Internal server error");
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("handleNotFound - Should handle different messages")
  void handleNotFound_DifferentMessages() {
    NotFoundException exception1 = new NotFoundException("User not found");
    NotFoundException exception2 = new NotFoundException("Product not found");

    StepVerifier.create(exceptionHandler.handleNotFound(exception1))
        .assertNext(response -> {
          assertThat(response.getBody().get("error")).isEqualTo("User not found");
        })
        .verifyComplete();

    StepVerifier.create(exceptionHandler.handleNotFound(exception2))
        .assertNext(response -> {
          assertThat(response.getBody().get("error")).isEqualTo("Product not found");
        })
        .verifyComplete();
  }
}
