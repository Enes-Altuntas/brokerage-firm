package com.inghubs.adapters.order.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.factory.OrderCancelRequestedCommandBuilder;
import com.inghubs.adapters.order.event.consumer.factory.OrderCreatedCommandBuilder;
import com.inghubs.adapters.order.event.consumer.factory.OrderMatchRequestedCommandBuilder;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.command.BeanAwareCommandPublisher;
import com.inghubs.common.command.OutboxCommandBuilder;
import com.inghubs.common.model.Command;
import com.inghubs.outbox.port.OutboxPort;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventConsumer extends BeanAwareCommandPublisher {

  public static final String OPERATION = "op";
  public static final String CREATE = "c";
  public static final String AFTER = "after";
  private final Map<String, OutboxCommandBuilder> builders;
  private final ObjectMapper objectMapper;
  private final OutboxPort outboxPort;

  public OrderEventConsumer(ObjectMapper objectMapper, OutboxPort outboxPort) {
    this.builders = Map.of(
        "ORDER_CREATED", new OrderCreatedCommandBuilder(),
        "ORDER_MATCH_REQUESTED", new OrderMatchRequestedCommandBuilder(),
        "ORDER_CANCEL_REQUESTED", new OrderCancelRequestedCommandBuilder()
    );
    this.objectMapper = objectMapper;
    this.outboxPort = outboxPort;
  }

  @RetryableTopic(
      backoff = @Backoff(delay = 2000, multiplier = 3, maxDelay = 20000)
  )
  @KafkaListener(topics = "order.public.outbox", groupId = "asset-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeOrderCreatedEvent(@Headers Map<String, Object> headers, String event, Acknowledgment acknowledgment) {
    log.info("Received order event: {}", event);
    try {
      OutboxEvent outboxEvent = getOutboxEvent(event,
          acknowledgment);

      if (outboxEvent == null) {
        acknowledgment.acknowledge();
        return;
      }

      OutboxCommandBuilder builder = builders.get(outboxEvent.getEventType());
      if (builder == null) {
        log.warn("No command builder found for event type: {}. Skipping event.", outboxEvent.getEventType());
        acknowledgment.acknowledge();
        return;
      }

      Command command = builder.build(outboxEvent, objectMapper);
      publish(command);
      acknowledgment.acknowledge();
      log.info("Successfully processed order event for aggregateId: {}", outboxEvent.getAggregateId());

    } catch (Exception e) {
      log.error("Error processing order event: {}", event, e);
      throw new RuntimeException("Error processing Debezium message", e);
    }
  }

  @KafkaListener(topics = "order.public.outbox-dlt", groupId = "asset-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeCreateOrderOutboxEventDLT(@Headers Map<String, Object> headers, String eventPayload,
      Acknowledgment acknowledgment) {
    log.error("Received DLT message for order event: {}", eventPayload);

    try {
      OutboxEvent outboxEvent = getOutboxEvent(eventPayload, acknowledgment);
      if (outboxEvent == null) {
        acknowledgment.acknowledge();
        return;
      }

      log.info("Sending compensation event for : {}", outboxEvent.getEventType() );

      String outboxEventType;
      if(outboxEvent.getEventType().equals("ORDER_CREATED")) {
        outboxEventType = "ORDER_REJECTED";
      } else if(outboxEvent.getEventType().equals("ORDER_CANCEL_REQUESTED")) {
        outboxEventType = "ORDER_CANCEL_REJECTED";
      } else {
        outboxEventType = "ORDER_MATCH_REJECTED";
      }

      outboxPort.createOrderOutboxEntity(
          outboxEventType,
          outboxEvent.getAggregateId(),
          outboxEvent.getPayload()
      );
    } catch (Exception e) {
      log.error("Error processing order event: {}", eventPayload, e);
    } finally {
      acknowledgment.acknowledge();
    }
  }

  private OutboxEvent getOutboxEvent(String event, Acknowledgment acknowledgment)
      throws JsonProcessingException {
    JsonNode rootNode = objectMapper.readTree(event);

    if (!CREATE.equals(rootNode.get(OPERATION).asText())) {
      log.warn("Skipping event as operation is not '{}': {}", CREATE, event);
      acknowledgment.acknowledge();
      return null;
    }

    return objectMapper.treeToValue(rootNode.get(AFTER), OutboxEvent.class);
  }
}
