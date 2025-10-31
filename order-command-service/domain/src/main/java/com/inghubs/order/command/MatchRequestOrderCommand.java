package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchRequestOrderCommand implements Command {

  private UUID buyOrderId;

  private UUID sellOrderId;

  private UUID buyerCustomerId;

  private UUID sellerCustomerId;

  private String assetName;

  private BigDecimal matchPrice;

  private BigDecimal matchSize;

}
