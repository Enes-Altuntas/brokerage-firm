package com.inghubs.adapters.inbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.inbox.jpa.entity.InboxEntity;
import com.inghubs.adapters.inbox.jpa.repository.InboxRepository;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboxDataAdapter implements InboxPort {

  public static final String ORDER = "ORDER";
  public static final String SYSTEM = "SYSTEM";
  private final InboxRepository inboxRepository;
  private final ObjectMapper objectMapper;

  @Override
  public Inbox retrieveInboxById(UUID id) {
    log.info("Retrieving inbox by id: {}", id);
    Optional<InboxEntity> entity = inboxRepository.findById(id);

    if(entity.isEmpty()) {
      log.warn("Inbox not found for id: {}", id);
      return null;
    }

    return entity.map(InboxEntity::toDomain).orElse(null);
  }

  @Override
  public void createInboxEntity(UUID outboxId, String eventType, UUID aggregateId, Object payloadObject) {
    log.info("Creating inbox entity for outboxId: {}, eventType: {}, aggregateId: {}", outboxId, eventType, aggregateId);
    JsonNode payload = objectMapper.valueToTree(payloadObject);

    InboxEntity entity = InboxEntity.builder()
        .id(outboxId)
        .aggregateId(aggregateId)
        .payload(payload)
        .eventType(eventType)
        .aggregateType(ORDER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy(SYSTEM)
        .updatedBy(SYSTEM)
        .build();

    inboxRepository.save(entity);
    log.info("Successfully created inbox entity for outboxId: {}", outboxId);
  }
}
