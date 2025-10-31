package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class UpdateOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<UpdateOrderCommand> {

  public static final String ORDER_UPDATED = "ORDER_UPDATED";
  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  private static final String ORDER_REJECTED = "ORDER_REJECTED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;
  private final LockPort lockPort;
  private final TransactionTemplate transactionTemplate;

  public UpdateOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort, InboxPort inboxPort,
      LockPort lockPort,
      TransactionTemplate transactionTemplate) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.inboxPort = inboxPort;
    this.lockPort = lockPort;
    this.transactionTemplate = transactionTemplate;
    register(UpdateOrderCommand.class, this);
  }

  @Override
  public void handle(UpdateOrderCommand command) {
    log.info("Handling update order command: {}", command);
    lockPort.execute(() -> {

      Inbox inbox = inboxPort.retrieveInboxById(command.getOutboxId());
      if (inbox != null) {
        log.info("Inbox already contains outboxId: {}, skipping update order command", command.getOutboxId());
        return;
      }

      Order order = orderPort.retrieveOrder(command.getOrder().getId(),
          command.getOrder().getCustomerId());

      if (order == null) {
        log.error("Order not found for orderId: {}", command.getOrder().getId());
        throw new OrderBusinessException("2000");
      }

      if (order.getStatus() != OrderStatus.INIT) {
        log.error("Order status is not INIT for orderId: {}", order.getId());
        throw new OrderBusinessException("2001");
      }

      if (command.getEventType().equals(ORDER_VALIDATED)) {
        order.reserve();
        log.info("Order reserved for orderId: {}", order.getId());
      } else if (command.getEventType().equals(ORDER_REJECTED)) {
        order.reject();
        log.info("Order rejected for orderId: {}", order.getId());
      }

      transactionTemplate.executeWithoutResult(status -> {
        orderPort.createOrUpdateOrder(order);
        inboxPort.createInboxEntity(command.getOutboxId(), command.getEventType(),
            command.getOrder().getId(), order);
        outboxPort.createOrderOutboxEntity(ORDER_UPDATED, order.getId(), order);
        log.info("Update order command processed successfully for orderId: {}", order.getId());
      });

    }, command.getOrder().getId().toString());
  }
}
