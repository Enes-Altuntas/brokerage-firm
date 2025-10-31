package com.inghubs.order.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.common.command.CommandHandler;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateOrderCommandHandler extends ObservableCommandPublisher
    implements CommandHandler<Order,CreateOrderCommand> {

  public static final String ORDER_CREATED = "ORDER_CREATED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final ObjectMapper objectMapper;

  public CreateOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort,
      ObjectMapper objectMapper) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.objectMapper = objectMapper;
    register(CreateOrderCommand.class, this);
  }

  @Override
  @Transactional
  public Order handle(CreateOrderCommand command) {

    Order initializedOrder = Order.initializeOrder(command);

    Order order = orderPort.createOrUpdateOrder(initializedOrder);

    outboxPort.createOrderOutboxEntity(ORDER_CREATED, order.getId(), order);

    return order;
  }
}
