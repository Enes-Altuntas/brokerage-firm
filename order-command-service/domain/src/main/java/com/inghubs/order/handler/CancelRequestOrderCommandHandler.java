package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.CancelRequestOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CancelRequestOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CancelRequestOrderCommand> {

  public static final String ORDER_CANCEL_REQUESTED = "ORDER_CANCEL_REQUESTED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final LockPort lockPort;
  private final TransactionTemplate transactionTemplate;

  public CancelRequestOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort,
      LockPort lockPort, TransactionTemplate transactionTemplate) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.lockPort = lockPort;
    this.transactionTemplate = transactionTemplate;
    register(CancelRequestOrderCommand.class, this);
  }

  @Override
  public void handle(CancelRequestOrderCommand command) {
    lockPort.lock(command.getOrderId());

    Order order = orderPort.retrieveOrder(command.getOrderId(), command.getCustomerId());

    if (order == null) {
      throw new OrderBusinessException("2000");
    }

    if(order.getStatus() != OrderStatus.PENDING) {
      throw new OrderBusinessException("2001");
    }

    order.requestCancel();

    transactionTemplate.executeWithoutResult(status -> {
      orderPort.createOrUpdateOrder(order);
      outboxPort.createOrderOutboxEntity(ORDER_CANCEL_REQUESTED, order);
    });

    lockPort.unlock(command.getOrderId());
  }
}
