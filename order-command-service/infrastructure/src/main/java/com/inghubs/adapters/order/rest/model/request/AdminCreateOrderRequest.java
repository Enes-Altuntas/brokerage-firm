package com.inghubs.adapters.order.rest.model.request;

import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.enums.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AdminCreateOrderRequest(

    @NotNull(message = "1002")
    UUID customerId,

    @NotBlank(message = "1002")
    String assetName,

    @NotNull(message = "1002")
    OrderSide side,

    @NotNull(message = "1002")
    BigDecimal price,

    @NotNull(message = "1002")
    BigDecimal size

) {
  public CreateOrderCommand toCommand() {
    return CreateOrderCommand.builder()
        .customerId(customerId)
        .assetName(assetName)
        .side(side)
        .price(price)
        .size(size)
        .build();
  }
}
