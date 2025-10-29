package com.inghubs.adapters.inbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.inbox.jpa.entity.InboxEntity;
import com.inghubs.adapters.inbox.jpa.repository.InboxRepository;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.command.UpdateOrderCommand;
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
  public void createOrderCreatedInboxEntity(CreateOrderCommand command) {
    JsonNode payload = objectMapper.valueToTree(command.getOrder());

    InboxEntity entity = InboxEntity.builder()
        .id(command.getOutboxId())
        .aggregateId(command.getOrder().getId())
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

  @Override
  public void createOrderUpdatedInboxEntity(UpdateOrderCommand command) {
    JsonNode payload = objectMapper.valueToTree(command.getOrder());

    InboxEntity entity = InboxEntity.builder()
        .id(command.getOutboxId())
        .aggregateId(command.getOrder().getId())
        .payload(payload)
        .eventType("ORDER_UPDATED")
        .aggregateType("ORDER")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();

    inboxRepository.save(entity);
  }

  @Override
  public void createOrderCanceledInboxEntity(CancelOrderCommand command) {
    JsonNode payload = objectMapper.valueToTree(command.getOrder());

    InboxEntity entity = InboxEntity.builder()
        .id(command.getOutboxId())
        .aggregateId(command.getOrder().getId())
        .payload(payload)
        .eventType("ORDER_CANCELED")
        .aggregateType("ORDER")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();

    inboxRepository.save(entity);
  }
}
