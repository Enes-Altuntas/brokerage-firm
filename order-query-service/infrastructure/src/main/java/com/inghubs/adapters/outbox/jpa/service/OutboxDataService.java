package com.inghubs.adapters.outbox.jpa.service;

import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;

public interface OutboxDataService {

  void save(OutboxEntity entity);
}
