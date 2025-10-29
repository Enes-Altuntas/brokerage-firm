package com.inghubs.order.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.port.OrderPort;
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
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (currentInbox != null) {
      return;
    }

    orderPort.createOrder(command.getOrder());
    inboxPort.createInboxForOrderCreatedEventEntity(command);
  }
}
