package com.inghubs.order.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateOrderCommandHandlerTest {

  @InjectMocks
  private CreateOrderCommandHandler createOrderCommandHandler;
  @Mock
  private InboxPort inboxPort;
  @Mock
  private OrderPort orderPort;

  @Test
  void shouldCreateOrderAndInboxWhenInboxDoesNotExist() {
    // Given
    CreateOrderCommand command = CreateOrderCommand.builder()
        .outboxId(UUID.randomUUID())
        .order(new Order())
        .build();

    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(null);

    // When
    createOrderCommandHandler.handle(command);

    // Then
    verify(orderPort).createOrUpdateOrder(command.getOrder());
    verify(inboxPort).createOrderCreatedInboxEntity(command);
  }

  @Test
  void shouldNotCreateOrderAndInboxWhenInboxExists() {
    // Given
    CreateOrderCommand command = CreateOrderCommand.builder()
        .outboxId(UUID.randomUUID())
        .order(new Order())
        .build();

    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(mock(Inbox.class));

    // When
    createOrderCommandHandler.handle(command);

    // Then
    verify(orderPort, never()).createOrUpdateOrder(command.getOrder());
    verify(inboxPort, never()).createOrderCreatedInboxEntity(command);
  }
}