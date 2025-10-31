package com.inghubs.adapters.order.event.consumer.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.asset.command.MatchOrderCommand;
import com.inghubs.common.command.OutboxCommandBuilder;
import com.inghubs.common.model.Command;
import com.inghubs.order.model.MatchOrder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderMatchRequestedCommandBuilder implements OutboxCommandBuilder {

  @Override
  public String getEventType() {
    return "ORDER_MATCH_REQUESTED";
  }

  @Override
  public Command build(OutboxEvent event, ObjectMapper mapper) {
    try {
      MatchOrder matchOrder = mapper.readValue(event.getPayload().asText(), MatchOrder.class);
      return MatchOrderCommand.builder()
          .outboxId(event.getId())
          .aggregateId(event.getAggregateId())
          .matchOrder(matchOrder)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to build command", e);
    }
  }
}
