package com.inghubs.adapters.order.event.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.command.BeanAwareCommandPublisher;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import java.util.Map;
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
public class OrderCreatedEventConsumer extends BeanAwareCommandPublisher {

  public static final String OPERATION = "op";
  public static final String CREATE = "c";
  public static final String AFTER = "after";
  private final ObjectMapper objectMapper;

  @RetryableTopic(
      backoff = @Backoff(delay = 2000, multiplier = 3, maxDelay = 20000)
  )
  @KafkaListener(topics = "order.public.outbox", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeOrderCreatedEvent(@Headers Map<String, Object> headers, String event, Acknowledgment acknowledgment) {
    log.info("Received order event {}", event);

    try {
      JsonNode rootNode = objectMapper.readTree(event);

      if (!CREATE.equals(rootNode.get(OPERATION).asText())) {
        acknowledgment.acknowledge();
        return;
      }

      OutboxEvent outboxEvent = objectMapper.treeToValue(rootNode.get(AFTER), OutboxEvent.class);

      if(!outboxEvent.getEventType().equals("ORDER_CREATED")) {
        acknowledgment.acknowledge();
        return;
      }

      Order order = objectMapper.readValue(outboxEvent.getPayload().asText(), Order.class);
      CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
          .outboxId(outboxEvent.getId())
          .order(order)
          .build();

      publish(createOrderCommand);
      acknowledgment.acknowledge();

    } catch (Exception e) {
      log.error("Error processing Debezium message, will retry: {}", e.getMessage());
      throw new RuntimeException("Error processing Debezium message", e);
    }
  }

  @KafkaListener(topics = "order.public.outbox-dlt", groupId = "order-query-group", containerFactory = "kafkaListenerContainerFactory")
  public void consumeCreateOrderOutboxEventDLT(@Headers Map<String, Object> headers, String eventPayload,
      Acknowledgment acknowledgment) {
    log.info("Received order event {}", eventPayload);
    acknowledgment.acknowledge();
  }
}
