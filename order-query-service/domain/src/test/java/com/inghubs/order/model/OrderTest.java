package com.inghubs.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.inghubs.order.model.enums.OrderStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTest {

  @Test
  void shouldUpdateOrderStatus() {
    // Given
    Order order = new Order();
    OrderStatus newStatus = OrderStatus.MATCHED;

    // When
    order.updateStatus(newStatus);

    // Then
    assertEquals(newStatus, order.getStatus());
    assertNotNull(order.getUpdatedAt());
    assertEquals("SYSTEM", order.getUpdatedBy());
  }

  @Test
  void shouldSetUpdatedAtToCurrentTime() {
    // Given
    Order order = new Order();
    OrderStatus newStatus = OrderStatus.PENDING;
    Instant beforeUpdate = Instant.now();

    // When
    order.updateStatus(newStatus);

    // Then
    Instant afterUpdate = Instant.now();
    assertNotNull(order.getUpdatedAt());
    assertEquals(true, !order.getUpdatedAt().isBefore(beforeUpdate));
    assertEquals(true, !order.getUpdatedAt().isAfter(afterUpdate));
  }
}