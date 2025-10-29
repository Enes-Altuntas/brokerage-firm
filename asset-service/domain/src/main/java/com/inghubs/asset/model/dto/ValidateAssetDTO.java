package com.inghubs.asset.model.dto;

import com.inghubs.order.model.enums.OrderSide;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateAssetDTO {

  private UUID assetId;

  private UUID customerId;

  private String side;

  private BigDecimal size;

  private BigDecimal price;

}
