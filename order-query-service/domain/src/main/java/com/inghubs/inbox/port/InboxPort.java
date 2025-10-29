package com.inghubs.inbox.port;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.command.UpdateOrderCommand;
import java.util.UUID;

public interface InboxPort {

  Inbox retrieveInboxById(UUID id);

  void createOrderCreatedInboxEntity(CreateOrderCommand command);

  void createOrderUpdatedInboxEntity(UpdateOrderCommand command);

  void createOrderCanceledInboxEntity(CancelOrderCommand command);

}
