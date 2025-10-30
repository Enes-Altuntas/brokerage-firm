package com.inghubs.asset.model;

import com.inghubs.order.model.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

  private UUID id;

  private UUID customerId;

  private String assetName;

  private BigDecimal size;

  private BigDecimal usableSize;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  private String createdBy;

  private String updatedBy;

  public void reserveForBuyOrder(Order order) {
    BigDecimal totalPrice = order.getSize().multiply(order.getPrice());
    this.usableSize = this.usableSize.subtract(totalPrice);
  }

  public void rollbackForBuyOrder(Order order) {
    BigDecimal totalPrice = order.getSize().multiply(order.getPrice());
    this.usableSize = this.usableSize.add(totalPrice);
  }

  public void reserveForSellOrder(Order order) {
    this.usableSize = this.usableSize.subtract(order.getSize());
  }

  public void rollbackForSellOrder(Order order) {
    this.usableSize = this.usableSize.add(order.getSize());
  }
}
