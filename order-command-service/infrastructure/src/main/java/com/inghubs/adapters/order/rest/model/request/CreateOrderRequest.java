package com.inghubs.adapters.order.rest.model.request;

import com.inghubs.order.command.CreateOrderCommand;

public record CreateOrderRequest(

) {
  public CreateOrderCommand toCommand() {
    return CreateOrderCommand.builder().build();
  }
}
