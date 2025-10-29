package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import org.springframework.stereotype.Service;

@Service
public class CancelOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CancelOrderCommand> {

  private final InboxPort inboxPort;
  private final OrderPort orderPort;

  public CancelOrderCommandHandler(InboxPort inboxPort, OrderPort orderPort) {
    this.inboxPort = inboxPort;
    this.orderPort = orderPort;
    register(CancelOrderCommand.class, this);
  }

  @Override
  public void handle(CancelOrderCommand command) {
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (currentInbox != null) {
      return;
    }

    Order order = orderPort.retrieveOrder(command.getOrder().getId());
    order.updateStatus(OrderStatus.CANCELED);

    orderPort.createOrUpdateOrder(command.getOrder());
    inboxPort.createOrderCanceledInboxEntity(command);
  }
}
