package com.inghubs.order.model;

import java.math.BigDecimal;
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
public class MatchOrder {

  private UUID buyOrderId;

  private UUID sellOrderId;

  private UUID buyerCustomerId;

  private UUID sellerCustomerId;

  private String assetName;

  private BigDecimal matchPrice;

  private BigDecimal matchSize;

  private BigDecimal priceDifference;

}
