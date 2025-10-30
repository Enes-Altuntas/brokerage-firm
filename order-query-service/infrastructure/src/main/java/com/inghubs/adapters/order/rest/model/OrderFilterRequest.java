package com.inghubs.adapters.order.rest.model;

import com.inghubs.order.model.enums.OrderStatus;
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
public class OrderFilterRequest {

  private UUID orderId;

  private OrderStatus status;

  private String assetName;

  private UUID customerId;
}
