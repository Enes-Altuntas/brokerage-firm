package com.inghubs.adapters.order.rest.model;

import com.inghubs.adapters.order.elastic.document.OrderDocument;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderResponse(

    UUID id,

    UUID customerId,

    String assetName,

    OrderSide side,

    BigDecimal size,

    BigDecimal price,

    OrderStatus status,

    Instant createdAt,

    Instant updatedAt
) {

  public static OrderResponse toResponse(OrderDocument orderDocument) {
    return OrderResponse.builder()
        .id(orderDocument.getId())
        .customerId(orderDocument.getCustomerId())
        .assetName(orderDocument.getAssetName())
        .side(orderDocument.getSide())
        .size(orderDocument.getSize())
        .price(orderDocument.getPrice())
        .status(orderDocument.getStatus())
        .createdAt(orderDocument.getCreatedAt())
        .updatedAt(orderDocument.getUpdatedAt())
        .build();
  }
}
