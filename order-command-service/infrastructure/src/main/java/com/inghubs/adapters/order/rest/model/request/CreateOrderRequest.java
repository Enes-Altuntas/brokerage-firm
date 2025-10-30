package com.inghubs.adapters.order.rest.model.request;

import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.enums.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(

    @NotBlank
    String assetName,

    @NotNull
    OrderSide side,

    @NotNull
    BigDecimal price,

    @NotNull
    BigDecimal size

) {
  public CreateOrderCommand toCommand(UUID customerId) {
    return CreateOrderCommand.builder()
        .customerId(customerId)
        .assetName(assetName)
        .side(side)
        .price(price)
        .size(size)
        .build();
  }
}
