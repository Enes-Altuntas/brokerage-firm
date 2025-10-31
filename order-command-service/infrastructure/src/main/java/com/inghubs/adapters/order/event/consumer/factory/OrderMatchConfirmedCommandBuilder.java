package com.inghubs.adapters.order.event.consumer.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.command.OutboxCommandBuilder;
import com.inghubs.common.model.Command;
import com.inghubs.order.command.MatchOrderCommand;
import com.inghubs.order.model.MatchOrder;

public class OrderMatchConfirmedCommandBuilder implements OutboxCommandBuilder {

  @Override
  public String getEventType() {
    return "ORDER_MATCH_CONFIRMED";
  }

  @Override
  public Command build(OutboxEvent event, ObjectMapper mapper) {
    try {
      MatchOrder order = mapper.readValue(event.getPayload().asText(), MatchOrder.class);
      return MatchOrderCommand.builder()
          .outboxId(event.getId())
          .eventType(getEventType())
          .matchOrder(order)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to build command", e);
    }
  }
}
