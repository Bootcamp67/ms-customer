package com.bootcamp67.ms_customer.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardEventConsumer {

  @KafkaListener(
      topics = "card-events",
      groupId = "customer-service-group",
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void handleCardEvent(String message) {
    log.info("Received card event: {}", message);

    try {
      // TODO: Parse event and handle according to event type
      // Example events:
      // - CARD_CREATED → Increment customer's card count
      // - CARD_BLOCKED → Log security event

      log.info("Processing card event: {}", message);

    } catch (Exception e) {
      log.error("Error processing card event: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(
      topics = "payment-events",
      groupId = "customer-service-group"
  )
  public void handlePaymentEvent(String message) {
    log.info("Received payment event: {}", message);

    try {
      // TODO: Implement logic
      // Update customer spending patterns
      // Detect unusual spending for fraud prevention

    } catch (Exception e) {
      log.error("Error processing payment event: {}", e.getMessage(), e);
    }
  }

  @KafkaListener(
      topics = "card-status-events",
      groupId = "customer-service-group"
  )
  public void handleCardStatusEvent(String message) {
    log.info("Received card status event: {}", message);

    try {
      // TODO: Implement logic
      // If card blocked → Log security event
      // If multiple cards blocked → Flag customer for review

    } catch (Exception e) {
      log.error("Error processing card status event: {}", e.getMessage(), e);
    }
  }
}
