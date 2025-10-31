package com.inghubs.order.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateOrderCommandHandlerTest {

  @InjectMocks
  private UpdateOrderCommandHandler updateOrderCommandHandler;
  @Mock
  private InboxPort inboxPort;
  @Mock
  private OrderPort orderPort;

  @Test
  void shouldUpdateOrderAndCreateInboxWhenInboxDoesNotExist() {
    // Given
    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setStatus(OrderStatus.PENDING);

    UpdateOrderCommand command = UpdateOrderCommand.builder()
        .outboxId(UUID.randomUUID())
        .order(order)
        .build();

    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(null);
    when(orderPort.retrieveOrder(command.getOrder().getId())).thenReturn(order);

    // When
    updateOrderCommandHandler.handle(command);

    // Then
    verify(orderPort).createOrUpdateOrder(command.getOrder());
    verify(inboxPort).createInboxEntity(command);
  }

  @Test
  void shouldNotUpdateOrderAndCreateInboxWhenInboxExists() {
    // Given
    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setStatus(OrderStatus.PENDING);

    UpdateOrderCommand command = UpdateOrderCommand.builder()
        .outboxId(UUID.randomUUID())
        .order(order)
        .build();

    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(mock(Inbox.class));

    // When
    updateOrderCommandHandler.handle(command);

    // Then
    verify(orderPort, never()).createOrUpdateOrder(command.getOrder());
    verify(inboxPort, never()).createInboxEntity(command);
  }
}