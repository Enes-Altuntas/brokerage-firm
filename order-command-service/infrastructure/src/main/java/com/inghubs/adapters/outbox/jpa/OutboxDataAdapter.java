package com.inghubs.adapters.outbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.repository.OutboxRepository;
import com.inghubs.order.command.MatchRequestOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxDataAdapter implements OutboxPort {

  public static final String ORDER = "ORDER";
  public static final String SYSTEM = "SYSTEM";
  public static final String ORDER_MATCH_REQUESTED = "ORDER_MATCH_REQUESTED";
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Override
  public void createOrderOutboxEntity(String eventType, Order order) {
    JsonNode payload = objectMapper.valueToTree(order);

    OutboxEntity entity = OutboxEntity.builder()
        .payload(payload)
        .aggregateId(order.getId())
        .aggregateType(ORDER)
        .eventType(eventType)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy(SYSTEM)
        .updatedBy(SYSTEM)
        .build();

    outboxRepository.save(entity);
  }

  @Override
  public void createOrderOutboxEntity(MatchRequestOrderCommand command) {
    JsonNode payload = objectMapper.valueToTree(command);

    OutboxEntity entity = OutboxEntity.builder()
        .payload(payload)
        .aggregateId(UUID.randomUUID())
        .aggregateType(ORDER)
        .eventType(ORDER_MATCH_REQUESTED)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy(SYSTEM)
        .updatedBy(SYSTEM)
        .build();

    outboxRepository.save(entity);
  }
}
