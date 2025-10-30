package com.inghubs.adapters.inbox.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.inbox.jpa.entity.InboxEntity;
import com.inghubs.adapters.inbox.jpa.repository.InboxRepository;
import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxDataAdapter implements InboxPort {

  public static final String ORDER = "ORDER";
  public static final String SYSTEM = "SYSTEM";
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
  public void createInboxEntity(UpdateAssetCommand command) {
    JsonNode payload = objectMapper.valueToTree(command.getOrder());

    InboxEntity entity = InboxEntity.builder()
        .id(command.getOutboxId())
        .aggregateId(command.getOrder().getId())
        .payload(payload)
        .eventType(command.getEventType())
        .aggregateType(ORDER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy(SYSTEM)
        .updatedBy(SYSTEM)
        .build();

    inboxRepository.save(entity);
  }
}
