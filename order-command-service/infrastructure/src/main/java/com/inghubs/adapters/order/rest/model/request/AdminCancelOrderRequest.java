package com.inghubs.adapters.order.rest.model.request;

import com.inghubs.order.command.CancelRequestOrderCommand;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdminCancelOrderRequest(

    @NotNull(message = "1002")
    UUID customerId,

    @NotNull(message = "1002")
    UUID orderId

) {
  public CancelRequestOrderCommand toCommand() {
    return CancelRequestOrderCommand.builder()
        .customerId(customerId)
        .orderId(orderId)
        .build();
  }
}
