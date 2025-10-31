package com.inghubs.adapters.order.jpa.entity;

import com.inghubs.common.jpa.entity.BaseEntity;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID customerId;

  private String assetName;

  @Enumerated(EnumType.STRING)
  private OrderSide side;

  private BigDecimal size;

  private BigDecimal matchedSize;

  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  public OrderEntity(Order order) {
    this.id = order.getId();
    this.customerId = order.getCustomerId();
    this.assetName = order.getAssetName();
    this.side = order.getSide();
    this.matchedSize = order.getMatchedSize();
    this.size = order.getSize();
    this.price = order.getPrice();
    this.status = order.getStatus();
    setCreatedBy(order.getCreatedBy());
    setUpdatedBy(order.getUpdatedBy());
    setCreatedAt(order.getCreatedAt());
    setUpdatedAt(order.getUpdatedAt());
    setDeletedAt(order.getDeletedAt());
  }

  public Order toDomain() {
    return Order.builder()
        .id(id)
        .customerId(customerId)
        .assetName(assetName)
        .side(side)
        .size(size)
        .matchedSize(matchedSize)
        .price(price)
        .status(status)
        .createdBy(getCreatedBy())
        .updatedBy(getUpdatedBy())
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .deletedAt(getDeletedAt())
        .build();
  }
}