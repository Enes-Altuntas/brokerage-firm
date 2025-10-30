package com.inghubs.adapters.order.event.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.command.BeanAwareCommandPublisher;
import com.inghubs.common.model.Command;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.model.Order;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer extends BeanAwareCommandPublisher {

  public static final String OPERATION = "op";
  public static final String CREATE = "c";
  public static final String AFTER = "after";
  public static final String ORDER_CREATED = "ORDER_CREATED";
  private static final Set<String> EVENTS_TO_ALLOWED = Set.of(
      "ORDER_CREATED",
      "ORDER_UPDATED",
      "ORDER_CANCELED",
      "ORDER_CANCEL_REQUESTED"
  );
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      backoff = @Backoff(delay = 2000, multiplier = 3, maxDelay = 20000)
  )
  @KafkaListener(topics = "order.public.outbox", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeOrderCreatedEvent(@Headers Map<String, Object> headers, String event, Acknowledgment acknowledgment) {
    try {
      JsonNode rootNode = objectMapper.readTree(event);

      if (!CREATE.equals(rootNode.get(OPERATION).asText())) {
        acknowledgment.acknowledge();
        return;
      }

      OutboxEvent outboxEvent = objectMapper.treeToValue(rootNode.get(AFTER), OutboxEvent.class);

      if(!EVENTS_TO_ALLOWED.contains(outboxEvent.getEventType())) {
        acknowledgment.acknowledge();
        return;
      }

      Order order = objectMapper.readValue(outboxEvent.getPayload().asText(), Order.class);
      Command command = buildCommand(outboxEvent, order);

      publish(command);
      acknowledgment.acknowledge();

    } catch (Exception e) {
      throw new RuntimeException("Error processing Debezium message", e);
    }
  }

  @KafkaListener(topics = "order.public.outbox-dlt", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeCreateOrderOutboxEventDLT(@Headers Map<String, Object> headers, String eventPayload,
      Acknowledgment acknowledgment) {
    acknowledgment.acknowledge();
  }

  private Command buildCommand(OutboxEvent outboxEvent, Order order) {
    if(outboxEvent.getEventType().equals(ORDER_CREATED)) {
      return CreateOrderCommand.builder()
          .outboxId(outboxEvent.getId())
          .eventType(outboxEvent.getEventType())
          .order(order)
          .build();
    } else {
      return UpdateOrderCommand.builder()
          .outboxId(outboxEvent.getId())
          .eventType(outboxEvent.getEventType())
          .order(order)
          .build();
    }
  }
}
