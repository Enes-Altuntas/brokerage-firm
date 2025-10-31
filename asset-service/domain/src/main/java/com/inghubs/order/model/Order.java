package com.inghubs.order.model;

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

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  private UUID id;

  private UUID customerId;

  private String assetName;

  private OrderSide side;

  private BigDecimal size;

  private BigDecimal matchedSize;

  private BigDecimal price;

  private OrderStatus status;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  private String createdBy;

  private String updatedBy;

}
