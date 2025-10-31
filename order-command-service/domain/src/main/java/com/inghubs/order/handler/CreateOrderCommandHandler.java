package com.inghubs.order.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.common.command.CommandHandler;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CreateOrderCommandHandler extends ObservableCommandPublisher
    implements CommandHandler<Order, CreateOrderCommand> {

  public static final String ORDER_CREATED = "ORDER_CREATED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;

  public CreateOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort,
      ObjectMapper objectMapper) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    register(CreateOrderCommand.class, this);
  }

  @Override
  @Transactional
  public Order handle(CreateOrderCommand command) {
    log.info("Handling create order command: {}", command);

    Order initializedOrder = Order.initializeOrder(command);

    Order order = orderPort.createOrUpdateOrder(initializedOrder);

    outboxPort.createOrderOutboxEntity(ORDER_CREATED, order.getId(), order);

    log.info("Create order command handled successfully for orderId: {}", order.getId());
    return order;
  }
}
