package com.inghubs.order.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateOrderCommandHandlerTest {

    private CreateOrderCommandHandler commandHandler;

    @Mock
    private OrderPort orderPort;

    @Mock
    private OutboxPort outboxPort;

    @BeforeEach
    void setUp() {
        commandHandler = new CreateOrderCommandHandler(orderPort, outboxPort, new ObjectMapper());
    }

    @Test
    void shouldCreateOrderAndPublishEvent() {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(UUID.randomUUID())
                .assetId(UUID.randomUUID())
                .assetName("BTC")
                .side(OrderSide.BUY)
                .price(BigDecimal.valueOf(50000))
                .size(BigDecimal.valueOf(1))
                .build();

        Order expectedOrder = Order.initializeOrder(command);
        when(orderPort.createOrUpdateOrder(any(Order.class))).thenReturn(expectedOrder);

        Order createdOrder = commandHandler.handle(command);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getCustomerId()).isEqualTo(command.getCustomerId());
        assertThat(createdOrder.getAssetName()).isEqualTo(command.getAssetName());
        assertThat(createdOrder.getSide()).isEqualTo(command.getSide());
        assertThat(createdOrder.getPrice()).isEqualTo(command.getPrice());
        assertThat(createdOrder.getSize()).isEqualTo(command.getSize());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPort).createOrUpdateOrder(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(com.inghubs.order.model.enums.OrderStatus.INIT);

        verify(outboxPort).createOrderOutboxEntity(eq(CreateOrderCommandHandler.ORDER_CREATED), any(Order.class));
    }
}