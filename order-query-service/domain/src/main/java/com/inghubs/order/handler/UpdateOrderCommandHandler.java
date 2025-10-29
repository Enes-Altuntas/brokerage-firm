package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<UpdateOrderCommand> {

  private final InboxPort inboxPort;
  private final OrderPort orderPort;

  public UpdateOrderCommandHandler(InboxPort inboxPort, OrderPort orderPort) {
    this.inboxPort = inboxPort;
    this.orderPort = orderPort;
    register(UpdateOrderCommand.class, this);
  }

  @Override
  public void handle(UpdateOrderCommand command) {
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (currentInbox != null) {
      return;
    }

    Order order = orderPort.retrieveOrder(command.getOrder().getId());
    order.updateStatus(command.getOrder().getStatus());

    orderPort.createOrUpdateOrder(command.getOrder());
    inboxPort.createOrderUpdatedInboxEntity(command);
  }
}
