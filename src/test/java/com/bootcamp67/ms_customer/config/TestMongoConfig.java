package com.bootcamp67.ms_customer.config;

import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

/**
 * The type Test mongo config.
 */
@Configuration
public class TestMongoConfig {
  /**
   * Reactive mongo template reactive mongo template.
   *
   * @return the reactive mongo template
   */
  @Bean
  public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory factory, MongoConverter converter) {
    // Usa un mongo que no conecta a nada, solo para inicializar repos
    return new ReactiveMongoTemplate(
        factory,converter
    );
  }
}
