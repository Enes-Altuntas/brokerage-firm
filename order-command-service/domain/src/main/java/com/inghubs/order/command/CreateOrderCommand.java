package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import com.inghubs.order.model.enums.OrderSide;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderCommand implements Command {

  private UUID assetId;

  private UUID customerId;

  private String assetName;

  private OrderSide side;

  private BigDecimal price;

  private BigDecimal size;

}
