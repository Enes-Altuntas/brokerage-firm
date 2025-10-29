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
      if(command.getEventType().equals("ORDER_VALIDATED")) {
        order.reserve();
        inboxPort.createOrderValidatedInboxEntity(command.getOutboxId(), order);
      } else {
        order.reject();
        inboxPort.createOrderRejectedInboxEntity(command.getOutboxId(), order);
      }

      orderPort.createOrUpdateOrder(order);
      outboxPort.createOrderUpdatedOutboxEntity(order);
    });
  }
}
