package com.inghubs.order.handler;

import com.inghubs.common.command.CommandHandler;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderCommandHandler extends ObservableCommandPublisher
    implements CommandHandler<Order,CreateOrderCommand> {

  private final OrderPort orderPort;

  public CreateOrderCommandHandler(OrderPort orderPort) {
    this.orderPort = orderPort;
    register(CreateOrderCommand.class, this);
  }

  @Override
  public Order handle(CreateOrderCommand command) {

    Order initializedOrder = Order.initializeOrder(command);

    return orderPort.createOrder(initializedOrder);
  }
}
