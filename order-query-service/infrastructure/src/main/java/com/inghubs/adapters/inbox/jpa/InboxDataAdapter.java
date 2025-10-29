package com.inghubs.adapters.inbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.inbox.jpa.entity.InboxEntity;
import com.inghubs.adapters.inbox.jpa.repository.InboxRepository;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxDataAdapter implements InboxPort {

  private final InboxRepository inboxRepository;
  private final ObjectMapper objectMapper;

  @Override
  public Inbox retrieveInboxById(UUID id) {

    Optional<InboxEntity> entity = inboxRepository.findById(id);

    if(entity.isEmpty()) {
      return null;
    }

    return entity.map(InboxEntity::toDomain).orElse(null);
  }

  @Override
  public void createInboxForOrderCreatedEventEntity(UUID outboxId, Order order) {
    JsonNode payload = objectMapper.valueToTree(order);

    InboxEntity entity = InboxEntity.builder()
        .id(outboxId)
        .aggregateId(order.getId())
        .payload(payload)
        .eventType("ORDER_CREATED")
        .aggregateType("ORDER")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();

    inboxRepository.save(entity);
  }
}
