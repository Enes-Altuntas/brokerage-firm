package com.inghubs.adapters.order.rest.model.response;

import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreateOrderResponse(

    UUID id,

    OrderStatus status,

    Instant createdAt

) {

  public static CreateOrderResponse toResponse(Order order) {
    return CreateOrderResponse.builder()
        .id(order.getId())
        .status(order.getStatus())
        .createdAt(order.getCreatedAt())
        .build();
  }
}
