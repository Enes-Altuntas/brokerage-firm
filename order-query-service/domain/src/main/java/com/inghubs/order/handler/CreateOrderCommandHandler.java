package com.inghubs.order.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CreateOrderCommand> {

  private final InboxPort inboxPort;
  private final OrderPort orderPort;
  private final ObjectMapper objectMapper;

  public CreateOrderCommandHandler(InboxPort inboxPort, OrderPort orderPort,
      ObjectMapper objectMapper) {
    this.inboxPort = inboxPort;
    this.orderPort = orderPort;
    this.objectMapper = objectMapper;
    register(CreateOrderCommand.class, this);
  }

  @Override
  public void handle(CreateOrderCommand command) {
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getId());
    if (currentInbox != null) {
      return;
    }

    Order order;
    try {
      order = objectMapper.readValue(command.getPayload().asText(), Order.class);
    } catch (Exception e) {
      throw new RuntimeException();
    }

    orderPort.createOrder(order);

    Inbox inbox = prepareInboxForOrderCreatedEvent(command);
    inboxPort.createInbox(inbox);
  }

  private Inbox prepareInboxForOrderCreatedEvent(CreateOrderCommand command) {

    return Inbox.builder()
        .id(command.getId())
        .aggregateId(command.getAggregateId())
        .payload(command.getPayload())
        .eventType(command.getEventType())
        .aggregateType(command.getAggregateType())
        .isProcessed(true)
        .processedAt(Instant.now())
        .requestId(command.getRequestId())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .createdBy("SYSTEM")
        .updatedBy("SYSTEM")
        .build();
  }
}
