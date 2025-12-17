package com.bootcamp67.ms_customer.service.cache;

import com.bootcamp67.ms_customer.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCacheService {
  private final ReactiveRedisTemplate<String, Object> redisTemplate;

  private static final String CACHE_PREFIX = "customer:";
  private static final Duration CACHE_TTL = Duration.ofMinutes(30);

  public Mono<CustomerDTO> getConsumer(String customerId){
    String key = CACHE_PREFIX + customerId;
    return redisTemplate.opsForValue()
        .get(key)
        .map(obj->(CustomerDTO) obj)
        .doOnSuccess(dto->{
          if(dto!=null){
            log.info("Cache HIT for customer: {}", customerId);
          }else{
            log.info("Cache MISS for customer: {}",customerId);
          }
        })
        .doOnError(error->log.info("Error getting customer from cache: {}",error.getMessage()));
  }

  /**
   * Save customer to cache
   */
  public Mono<Boolean> saveCustomer(CustomerDTO customer) {
    String key = CACHE_PREFIX + customer.getId();

    return redisTemplate.opsForValue()
        .set(key, customer, CACHE_TTL)
        .doOnSuccess(result -> {
          if (result) {
            log.info("Customer cached: {} (TTL: {} minutes)", customer.getId(), CACHE_TTL.toMinutes());
          }
        })
        .doOnError(error -> log.error("Error saving customer to cache: {}", error.getMessage()))
        .onErrorReturn(false);
  }

  public Mono<Boolean> deleteCustomer(String customerId) {
    String key = CACHE_PREFIX + customerId;

    return redisTemplate.opsForValue()
        .delete(key)
        .doOnSuccess(result -> {
          if (result) {
            log.info("Customer removed from cache: {}", customerId);
          }
        })
        .doOnError(error -> log.error("Error deleting customer from cache: {}", error.getMessage()))
        .onErrorReturn(false);
  }

  public Mono<Void> clearAll() {
    return redisTemplate.keys(CACHE_PREFIX + "*")
        .flatMap(key -> redisTemplate.delete(key))
        .then()
        .doOnSuccess(v -> log.info("All customer cache cleared"))
        .doOnError(error -> log.error("Error clearing cache: {}", error.getMessage()));
  }

  public Mono<Boolean> exists(String customerId) {
    String key = CACHE_PREFIX + customerId;
    return redisTemplate.hasKey(key);
  }

  public Mono<Duration> getTTL(String customerId) {
    String key = CACHE_PREFIX + customerId;
    return redisTemplate.getExpire(key)
        .defaultIfEmpty(Duration.ZERO);
  }
}
