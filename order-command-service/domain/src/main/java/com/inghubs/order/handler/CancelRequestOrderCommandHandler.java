package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.order.command.CancelRequestOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import org.springframework.stereotype.Service;

@Service
public class CancelRequestOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CancelRequestOrderCommand> {

  public static final String ORDER_CANCEL_REQUESTED = "ORDER_CANCEL_REQUESTED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;

  public CancelRequestOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    register(CancelRequestOrderCommand.class, this);
  }

  @Override
  public void handle(CancelRequestOrderCommand command) {
    Order order = orderPort.retrieveOrder(command.getOrderId(), command.getCustomerId());

    if (order == null) {
      throw new RuntimeException();
    }

    if(order.getStatus() != OrderStatus.PENDING) {
      throw new RuntimeException();
    }

    order.requestCancel();

    orderPort.createOrUpdateOrder(order);
    outboxPort.createOrderOutboxEntity(ORDER_CANCEL_REQUESTED, order);
  }
}
