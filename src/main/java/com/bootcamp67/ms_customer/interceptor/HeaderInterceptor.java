package com.bootcamp67.ms_customer.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class HeaderInterceptor implements WebFilter {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String correlationId = exchange.getRequest()
        .getHeaders()
        .getFirst("x-correlation-id");

    if(correlationId==null){
      return Mono.error(new RuntimeException("Missing header x-correlation-id"));
    }

    exchange.getAttributes().put("correlationId",correlationId);
    return chain.filter(exchange);
  }
}
