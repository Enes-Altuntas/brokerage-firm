package com.inghubs.adapters.order.rest.model.request;

import com.inghubs.order.command.MatchRequestOrderCommand;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdminMatchOrderRequest(

    @NotNull(message = "1002")
    UUID buyOrderId,

    @NotNull(message = "1002")
    UUID sellOrderId

) {
  public MatchRequestOrderCommand toCommand() {
    return MatchRequestOrderCommand.builder()
        .buyOrderId(buyOrderId)
        .sellOrderId(sellOrderId)
        .build();
  }
}
