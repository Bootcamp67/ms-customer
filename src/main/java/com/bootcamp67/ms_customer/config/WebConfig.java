package com.bootcamp67.ms_customer.config;

import com.bootcamp67.ms_customer.interceptor.HeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

/**
 * The type Web config.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {
  private final HeaderInterceptor headerInterceptor;

  @Override
  public void addCorsMappings(CorsRegistry registry){
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("*");
  }

  /**
   * Add interceptor web filter.
   *
   * @return the web filter
   */
  @Bean
  public WebFilter addInterceptor(){
    return headerInterceptor;
  }
}
