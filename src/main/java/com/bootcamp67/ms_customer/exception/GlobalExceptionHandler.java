package com.bootcamp67.ms_customer.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The type Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle not found mono.
   *
   * @param exception the exception
   * @return the mono
   */
  @ExceptionHandler(NotFoundException.class)
  public Mono<ResponseEntity<Map<String,String>>> handleNotFound(NotFoundException exception){
    return Mono.just(ResponseEntity.status(404)
        .body(Map.of("error",exception.getMessage())));
  }

  /**
   * Handle general mono.
   *
   * @param exception the exception
   * @return the mono
   */
  @ExceptionHandler
  public Mono<ResponseEntity<Map<String,String>>> handleGeneral(Exception exception){
    return Mono.just(ResponseEntity.status(500)
        .body(Map.of("error",exception.getMessage())));
  }
}
