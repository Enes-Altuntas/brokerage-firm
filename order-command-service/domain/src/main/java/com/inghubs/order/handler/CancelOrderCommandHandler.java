package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CancelOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CancelOrderCommand> {

  public static final String ORDER_UPDATED = "ORDER_UPDATED";
  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  private static final String ORDER_CANCEL_CONFIRMED = "ORDER_CANCEL_CONFIRMED";
  private static final String ORDER_CANCEL_REJECTED = "ORDER_CANCEL_REJECTED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;
  private final LockPort lockPort;
  private final TransactionTemplate transactionTemplate;

  public CancelOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort, InboxPort inboxPort,
      LockPort lockPort,
      TransactionTemplate transactionTemplate) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.inboxPort = inboxPort;
    this.lockPort = lockPort;
    this.transactionTemplate = transactionTemplate;
    register(CancelOrderCommand.class, this);
  }

  @Override
  public void handle(CancelOrderCommand command) {
    lockPort.execute(() -> {

      Inbox inbox = inboxPort.retrieveInboxById(command.getOutboxId());
      if (inbox != null) {
        return;
      }

      Order order = orderPort.retrieveOrder(command.getOrder().getId(),
          command.getOrder().getCustomerId());

      if(!order.getStatus().equals(OrderStatus.CANCEL_REQUESTED)) {
        throw new OrderBusinessException("2001");
      }

      if (command.getEventType().equals(ORDER_CANCEL_CONFIRMED)) {
        order.cancel();
      } else if (command.getEventType().equals(ORDER_CANCEL_REJECTED)) {
        order.reserve();
      }

      transactionTemplate.executeWithoutResult(status -> {
        orderPort.createOrUpdateOrder(order);
        inboxPort.createInboxEntity(command.getOutboxId(), command.getEventType(),
            command.getOrder().getId(), order);
        outboxPort.createOrderOutboxEntity(ORDER_UPDATED, order.getId(), order);
      });

    }, command.getOrder().getId().toString());
  }
}
