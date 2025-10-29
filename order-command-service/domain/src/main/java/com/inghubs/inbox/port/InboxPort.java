package com.inghubs.inbox.port;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.order.model.Order;
import java.util.UUID;

public interface InboxPort {

  Inbox retrieveInboxById(UUID id);

  void createOrderValidatedInboxEntity(UUID outboxId, Order order);

  void createOrderRejectedInboxEntity(UUID outboxId, Order order);
}
