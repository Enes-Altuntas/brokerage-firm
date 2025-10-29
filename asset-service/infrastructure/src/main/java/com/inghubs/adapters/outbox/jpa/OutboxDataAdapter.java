package com.inghubs.adapters.outbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.repository.OutboxRepository;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.model.Outbox;
import com.inghubs.outbox.port.OutboxPort;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxDataAdapter implements OutboxPort {

  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Override
  public void createOrderRejectedOutboxEntity(Order order) {
    JsonNode payload = objectMapper.valueToTree(order);

    OutboxEntity entity = OutboxEntity.builder()
        .aggregateId(order.getId())
        .payload(payload)
        .eventType("ORDER_REJECTED")
        .aggregateType("ORDER")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();

    outboxRepository.save(entity);
  }

  @Override
  public void createOrderValidatedOutboxEntity(Order order) {
    JsonNode payload = objectMapper.valueToTree(order);

    OutboxEntity entity = OutboxEntity.builder()
        .aggregateId(order.getId())
        .payload(payload)
        .eventType("ORDER_VALIDATED")
        .aggregateType("ORDER")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();

    outboxRepository.save(entity);
  }
}
