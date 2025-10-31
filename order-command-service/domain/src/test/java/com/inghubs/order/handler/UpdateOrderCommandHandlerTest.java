package com.inghubs.order.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class UpdateOrderCommandHandlerTest {

    private UpdateOrderCommandHandler commandHandler;

    @Mock
    private OrderPort orderPort;

    @Mock
    private OutboxPort outboxPort;

    @Mock
    private InboxPort inboxPort;

    @Mock
    private LockPort lockPort;

    @Mock
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        commandHandler = new UpdateOrderCommandHandler(orderPort, outboxPort, inboxPort, lockPort, transactionTemplate);
    }

    @Test
    void shouldUpdateOrderToPendingWhenValidated() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID outboxId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.INIT);

        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .order(order)
                .outboxId(outboxId)
                .eventType(UpdateOrderCommandHandler.ORDER_VALIDATED)
                .build();

        when(inboxPort.retrieveInboxById(outboxId)).thenReturn(null);
        when(orderPort.retrieveOrder(orderId, customerId)).thenReturn(order);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(lockPort).execute(any(Runnable.class), any(String[].class));

        commandHandler.handle(command);

        verify(lockPort).execute(any(Runnable.class), org.mockito.ArgumentMatchers.eq(orderId.toString()));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderPort).createOrUpdateOrder(order);
        verify(inboxPort).createInboxEntity(command.getOutboxId(), command.getEventType(), command.getOrder().getId(), order);
        verify(outboxPort).createOrderOutboxEntity(UpdateOrderCommandHandler.ORDER_UPDATED, order.getId(), order);
    }

    @Test
    void shouldUpdateOrderToRejectedWhenRejected() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID outboxId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.INIT);

        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .order(order)
                .outboxId(outboxId)
                .eventType("ORDER_REJECTED")
                .build();

        when(inboxPort.retrieveInboxById(outboxId)).thenReturn(null);
        when(orderPort.retrieveOrder(orderId, customerId)).thenReturn(order);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        // make lockPort.execute run the runnable immediately
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(lockPort).execute(any(Runnable.class), any(String[].class));

        commandHandler.handle(command);

        verify(lockPort).execute(any(Runnable.class), org.mockito.ArgumentMatchers.eq(orderId.toString()));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED);
        verify(orderPort).createOrUpdateOrder(order);
        verify(inboxPort).createInboxEntity(command.getOutboxId(), command.getEventType(), command.getOrder().getId(), order);
        verify(outboxPort).createOrderOutboxEntity(UpdateOrderCommandHandler.ORDER_UPDATED, order.getId(), order);
    }

    @Test
    void shouldDoNothingIfInboxAlreadyProcessed() {
        UUID orderId = UUID.randomUUID();
        UUID outboxId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);

        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .order(order)
                .outboxId(outboxId)
                .build();

        when(inboxPort.retrieveInboxById(outboxId)).thenReturn(Inbox.builder().build());

        // make lockPort.execute run the runnable immediately
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(lockPort).execute(any(Runnable.class), any(String[].class));

        commandHandler.handle(command);

        verify(lockPort).execute(any(Runnable.class), org.mockito.ArgumentMatchers.eq(orderId.toString()));
        verify(orderPort, never()).retrieveOrder(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotInInitStatus() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID outboxId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.PENDING);

        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .order(order)
                .outboxId(outboxId)
                .eventType(UpdateOrderCommandHandler.ORDER_VALIDATED)
                .build();

        when(inboxPort.retrieveInboxById(outboxId)).thenReturn(null);
        when(orderPort.retrieveOrder(orderId, customerId)).thenReturn(order);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(lockPort).execute(any(Runnable.class), any(String[].class));

        assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(OrderBusinessException.class)
                .hasMessage("2001");

        verify(lockPort).execute(any(Runnable.class), org.mockito.ArgumentMatchers.eq(orderId.toString()));
    }
}
