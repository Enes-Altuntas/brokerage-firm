package com.inghubs.inbox.port;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.order.command.CreateOrderCommand;
import java.util.UUID;

public interface InboxPort {

  Inbox retrieveInboxById(UUID id);

  void createInboxForOrderCreatedEventEntity(CreateOrderCommand command);
}
