package com.bootcamp67.ms_customer.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * The type Header interceptor.
 */
@Component
public class HeaderInterceptor implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String correlationId = exchange.getRequest()
        .getHeaders()
        .getFirst("x-correlation-id");

    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }

    exchange.getAttributes().put("correlationId", correlationId);
    return chain.filter(exchange);
  }
}
