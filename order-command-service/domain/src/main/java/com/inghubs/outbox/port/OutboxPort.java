package com.inghubs.outbox.port;

import java.util.UUID;

public interface OutboxPort {

  void createOrderOutboxEntity(String eventType, UUID aggregateId, Object payloadObject);

}
