package com.bootcamp67.ms_customer.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

  @KafkaListener(
      topics = "transaction-events",
      groupId = "customer-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleTransactionEvent(String message) {
    log.info("Received transaction event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - TRANSACTION_PROCESSED → Update last activity timestamp
      // - LARGE_TRANSACTION → Flag for review if unusual
      // - SUSPICIOUS_TRANSACTION → Alert fraud team

      log.info("Processing transaction event: {}", message);

    } catch (Exception e) {
      log.error("Error processing transaction event: {}", e.getMessage(), e);
    }
  }
}
