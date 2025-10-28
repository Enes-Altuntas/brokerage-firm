package com.inghubs.adapters.outbox.jpa;

import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.service.OutboxDataService;
import org.springframework.stereotype.Component;

@Component
public class OutboxDataAdapter implements OutboxDataService {

  @Override
  public void save(OutboxEntity entity) {

  }
}
