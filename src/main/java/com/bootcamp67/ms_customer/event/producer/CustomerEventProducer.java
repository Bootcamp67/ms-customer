package com.bootcamp67.ms_customer.event.producer;

import com.bootcamp67.ms_customer.event.CustomerCreatedEvent;
import com.bootcamp67.ms_customer.event.CustomerEvent;
import com.bootcamp67.ms_customer.event.CustomerKYCVerifiedEvent;
import com.bootcamp67.ms_customer.event.CustomerStatusChangedEvent;
import com.bootcamp67.ms_customer.event.CustomerTierChangedEvent;
import com.bootcamp67.ms_customer.event.CustomerUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  // Kafka Topics
  private static final String CUSTOMER_EVENTS_TOPIC = "customer-events";
  private static final String CUSTOMER_STATUS_TOPIC = "customer-status-events";
  private static final String CUSTOMER_TIER_TOPIC = "customer-tier-events";
  private static final String CUSTOMER_KYC_TOPIC = "customer-kyc-events";

  public Mono<Void> publishCustomerCreated(CustomerCreatedEvent event) {
    log.info("Publishing customer created event for customer: {}", event.getCustomerId());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_CREATED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_EVENTS_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerUpdated(CustomerUpdatedEvent event) {
    log.info("Publishing customer updated event for customer: {}", event.getCustomerId());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_UPDATED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_EVENTS_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerDeleted(String customerId, String reason, String requestedBy) {
    log.info("Publishing customer deleted event for customer: {}", customerId);

    CustomerStatusChangedEvent statusEvent = CustomerStatusChangedEvent.builder()
        .customerId(customerId)
        .previousStatus("ACTIVE")
        .newStatus("DELETED")
        .reason(reason)
        .requestedBy(requestedBy)
        .changedAt(LocalDateTime.now())
        .build();

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_DELETED)
        .customerId(customerId)
        .timestamp(LocalDateTime.now())
        .payload(statusEvent)
        .build();

    return sendEvent(CUSTOMER_EVENTS_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerBlocked(CustomerStatusChangedEvent event) {
    log.info("Publishing customer blocked event for customer: {}", event.getCustomerId());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_BLOCKED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_STATUS_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerActivated(CustomerStatusChangedEvent event) {
    log.info("Publishing customer activated event for customer: {}", event.getCustomerId());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_ACTIVATED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_STATUS_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerUpgraded(CustomerTierChangedEvent event) {
    log.info("Publishing customer upgraded event for customer: {} from {} to {}",
        event.getCustomerId(), event.getPreviousTier(), event.getNewTier());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_UPGRADED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_TIER_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  public Mono<Void> publishCustomerKYCVerified(CustomerKYCVerifiedEvent event) {
    log.info("Publishing customer KYC verified event for customer: {} level: {}",
        event.getCustomerId(), event.getVerificationLevel());

    CustomerEvent customerEvent = CustomerEvent.builder()
        .eventId(UUID.randomUUID().toString())
        .eventType(CustomerEvent.EventType.CUSTOMER_KYC_VERIFIED)
        .customerId(event.getCustomerId())
        .timestamp(LocalDateTime.now())
        .payload(event)
        .build();

    return sendEvent(CUSTOMER_KYC_TOPIC, customerEvent.getCustomerId(), customerEvent);
  }

  private Mono<Void> sendEvent(String topic, String key, Object event) {
    return Mono.create(sink -> {
      try {
        ListenableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(topic, key, event);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
          @Override
          public void onSuccess(SendResult<String, Object> result) {
            log.info("Event sent successfully to topic: {} partition: {} offset: {}",
                topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
            sink.success();
          }

          @Override
          public void onFailure(Throwable ex) {
            log.error("Error sending event to topic: {} error: {}", topic, ex.getMessage(), ex);
            sink.error(ex);
          }
        });
      } catch (Exception e) {
        log.error("Exception sending event to topic: {}", topic, e);
        sink.error(e);
      }
    });
  }
}
