package com.inghubs.adapters.order.event.consumer.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.command.OutboxCommandBuilder;
import com.inghubs.common.model.Command;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.command.UpdateOrderCommand;
import com.inghubs.order.model.Order;

public class OrderCancelConfirmedCommandBuilder implements OutboxCommandBuilder {

  @Override
  public String getEventType() {
    return "ORDER_CANCEL_CONFIRMED";
  }

  @Override
  public Command build(OutboxEvent event, ObjectMapper mapper) {
    try {
      Order order = mapper.readValue(event.getPayload().asText(), Order.class);
      return CancelOrderCommand.builder()
          .outboxId(event.getId())
          .eventType(getEventType())
          .order(order)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to build command", e);
    }
  }
}
