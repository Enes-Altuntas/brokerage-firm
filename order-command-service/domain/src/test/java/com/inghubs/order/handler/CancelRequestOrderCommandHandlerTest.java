package com.inghubs.order.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.CancelRequestOrderCommand;
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
class CancelRequestOrderCommandHandlerTest {

    private CancelRequestOrderCommandHandler commandHandler;

    @Mock
    private OrderPort orderPort;

    @Mock
    private OutboxPort outboxPort;

    @Mock
    private LockPort lockPort;

    @Mock
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        commandHandler = new CancelRequestOrderCommandHandler(orderPort, outboxPort, lockPort, transactionTemplate);
    }

    @Test
    void shouldRequestCancelForPendingOrder() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CancelRequestOrderCommand command = CancelRequestOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .build();

        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.PENDING);

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
        verify(orderPort).retrieveOrder(orderId, customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
        verify(orderPort).createOrUpdateOrder(order);
        verify(outboxPort).createOrderOutboxEntity(CancelRequestOrderCommandHandler.ORDER_CANCEL_REQUESTED, order.getId(), order);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CancelRequestOrderCommand command = CancelRequestOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .build();

        when(orderPort.retrieveOrder(orderId, customerId)).thenReturn(null);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(lockPort).execute(any(Runnable.class), any(String[].class));

        assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(OrderBusinessException.class)
                .hasMessage("2000");

        verify(lockPort).execute(any(Runnable.class), org.mockito.ArgumentMatchers.eq(orderId.toString()));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotPending() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        CancelRequestOrderCommand command = CancelRequestOrderCommand.builder()
                .orderId(orderId)
                .customerId(customerId)
                .build();

        Order order = new Order();
        order.setId(orderId);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.CANCELED);

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
