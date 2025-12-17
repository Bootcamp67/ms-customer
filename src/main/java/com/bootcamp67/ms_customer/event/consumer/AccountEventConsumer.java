package com.bootcamp67.ms_customer.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventConsumer {

  @KafkaListener(
      topics = "account-events",
      groupId = "customer-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleAccountEvent(String message) {
    log.info("Received account event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - ACCOUNT_CREATED → Increment customer's account count
      // - ACCOUNT_DELETED → Decrement customer's account count

      log.info("Processing account event: {}", message);

    } catch (Exception e) {
      log.error("Error processing account event: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(
      topics = "balance-events",
      groupId = "customer-service-group"
  )
  public void handleBalanceEvent(String message) {
    log.info("Received balance event: {}", message);

    try {
      // TODO: Implement logic
      // If balance low → Trigger notification to customer

    } catch (Exception e) {
      log.error("Error processing balance event: {}", e.getMessage(), e);
    }
  }
}
