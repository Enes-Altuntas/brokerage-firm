package com.inghubs.order.model;

import com.inghubs.common.model.BaseDomain;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Order extends BaseDomain {

  private UUID id;

  private UUID customerId;

  private UUID assetId;

  private String assetName;

  private OrderSide side;

  private BigDecimal size;

  private BigDecimal price;

  private OrderStatus status;

  public static Order initializeOrder(CreateOrderCommand command) {
    return Order.builder()
        .customerId(command.getCustomerId())
        .assetId(command.getAssetId())
        .assetName(command.getAssetName())
        .side(command.getSide())
        .size(command.getSize())
        .price(command.getPrice())
        .status(OrderStatus.INIT)
        .createdBy(command.getCustomerId().toString())
        .updatedBy(command.getCustomerId().toString())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }
}
