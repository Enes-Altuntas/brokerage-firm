package com.inghubs.order.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldInitializeOrderCorrectly() {
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId(UUID.randomUUID())
                .assetId(UUID.randomUUID())
                .assetName("ETH")
                .side(OrderSide.SELL)
                .price(BigDecimal.valueOf(3000))
                .size(BigDecimal.valueOf(10))
                .build();

        Order order = Order.initializeOrder(command);

        assertThat(order.getCustomerId()).isEqualTo(command.getCustomerId());
        assertThat(order.getAssetName()).isEqualTo(command.getAssetName());
        assertThat(order.getSide()).isEqualTo(command.getSide());
        assertThat(order.getPrice()).isEqualTo(command.getPrice());
        assertThat(order.getSize()).isEqualTo(command.getSize());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.INIT);
        assertThat(order.getCreatedBy()).isEqualTo(command.getCustomerId().toString());
        assertThat(order.getUpdatedBy()).isEqualTo(command.getCustomerId().toString());
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldReserveOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.INIT);

        order.reserve();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getUpdatedBy()).isEqualTo(Order.SYSTEM);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.INIT);

        order.reject();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED);
        assertThat(order.getUpdatedBy()).isEqualTo(Order.SYSTEM);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRequestCancel() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        order.requestCancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL_REQUESTED);
        assertThat(order.getUpdatedBy()).isEqualTo(Order.SYSTEM);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldCancelOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.CANCEL_REQUESTED);

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.getUpdatedBy()).isEqualTo(Order.SYSTEM);
        assertThat(order.getUpdatedAt()).isNotNull();
    }
}