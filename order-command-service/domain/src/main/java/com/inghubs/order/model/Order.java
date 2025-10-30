package com.inghubs.order.model;

import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  public static final String SYSTEM = "SYSTEM";
  private UUID id;

  private UUID customerId;

  private String assetName;

  private OrderSide side;

  private BigDecimal size;

  private BigDecimal price;

  private OrderStatus status;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  private String createdBy;

  private String updatedBy;

  public static Order initializeOrder(CreateOrderCommand command) {
    return Order.builder()
        .customerId(command.getCustomerId())
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

  public void reserve() {
    this.status = OrderStatus.PENDING;
    this.updatedBy = SYSTEM;
    this.updatedAt = Instant.now();
  }

  public void reject() {
    this.status = OrderStatus.REJECTED;
    this.updatedBy = SYSTEM;
    this.updatedAt = Instant.now();
  }

  public void requestCancel() {
    this.status = OrderStatus.CANCEL_REQUESTED;
    this.updatedBy = SYSTEM;
    this.updatedAt = Instant.now();
  }

  public void cancel() {
    this.status = OrderStatus.CANCELED;
    this.updatedBy = SYSTEM;
    this.updatedAt = Instant.now();
  }
}
