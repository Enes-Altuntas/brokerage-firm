package com.inghubs.adapters.outbox.jpa;

import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.repository.OutboxRepository;
import com.inghubs.adapters.outbox.jpa.service.OutboxDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxDataAdapter implements OutboxDataService {

  private final OutboxRepository outboxRepository;

  @Override
  public void save(OutboxEntity entity) {

    outboxRepository.save(entity);

  }
}
