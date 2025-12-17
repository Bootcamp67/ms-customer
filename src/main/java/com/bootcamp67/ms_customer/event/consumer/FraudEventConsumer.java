package com.bootcamp67.ms_customer.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEventConsumer {

  @KafkaListener(
      topics = "fraud-events",
      groupId = "customer-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleFraudEvent(String message) {
    log.info("Received fraud event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - FRAUD_DETECTED → Block customer immediately
      // - SUSPICIOUS_ACTIVITY → Flag for manual review
      // - FRAUD_CLEARED → Unblock customer

      log.info("Processing fraud event: {}", message);

    } catch (Exception e) {
      log.error("Error processing fraud event: {}", e.getMessage(), e);
    }
  }
}
