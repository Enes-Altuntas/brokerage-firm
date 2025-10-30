package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UpdateOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<UpdateOrderCommand> {

  public static final String ORDER_UPDATED = "ORDER_UPDATED";
  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  private static final String ORDER_CANCEL_CONFIRMED = "ORDER_CANCEL_CONFIRMED";
  private static final String ORDER_CANCEL_REJECTED = "ORDER_CANCEL_REJECTED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;
  private final TransactionTemplate transactionTemplate;

  public UpdateOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort, InboxPort inboxPort,
      TransactionTemplate transactionTemplate) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.inboxPort = inboxPort;
    this.transactionTemplate = transactionTemplate;
    register(UpdateOrderCommand.class, this);
  }

  @Override
  public void handle(UpdateOrderCommand command) {
    Inbox inbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (inbox != null) {
      return;
    }

    Order order = orderPort.retrieveOrder(command.getOrder().getId());

    transactionTemplate.executeWithoutResult(status -> {
      if(command.getEventType().equals(ORDER_VALIDATED)) {
        order.reserve();
      } else if (command.getEventType().equals(ORDER_CANCEL_CONFIRMED)) {
        order.cancel();
      } else if (command.getEventType().equals(ORDER_CANCEL_REJECTED)) {
        order.reserve();
      } else {
        order.reject();
      }

      orderPort.createOrUpdateOrder(order);
      inboxPort.createInboxEntity(command);
      outboxPort.createOrderOutboxEntity(ORDER_UPDATED, order);
    });
  }
}
