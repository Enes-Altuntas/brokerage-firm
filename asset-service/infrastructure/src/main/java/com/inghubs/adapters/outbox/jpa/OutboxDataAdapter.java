package com.inghubs.adapters.outbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.repository.OutboxRepository;
import com.inghubs.outbox.port.OutboxPort;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxDataAdapter implements OutboxPort {

  public static final String ORDER = "ORDER";
  public static final String SYSTEM = "SYSTEM";
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Override
  public void createOrderOutboxEntity(String eventType, UUID aggregateId, Object payloadObject) {
    log.info("Creating outbox entity for eventType: {}, aggregateId: {}", eventType, aggregateId);
    JsonNode payload = objectMapper.valueToTree(payloadObject);

    OutboxEntity entity = OutboxEntity.builder()
        .aggregateId(aggregateId)
        .payload(payload)
        .eventType(eventType)
        .aggregateType(ORDER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy(SYSTEM)
        .updatedBy(SYSTEM)
        .build();

    outboxRepository.save(entity);
    log.info("Successfully created outbox entity for eventType: {}, aggregateId: {}", eventType, aggregateId);
  }
}
