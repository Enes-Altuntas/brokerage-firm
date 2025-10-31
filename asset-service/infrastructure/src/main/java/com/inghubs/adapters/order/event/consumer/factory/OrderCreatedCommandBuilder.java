package com.inghubs.adapters.order.event.consumer.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.common.command.OutboxCommandBuilder;
import com.inghubs.common.model.Command;
import com.inghubs.order.model.Order;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderCreatedCommandBuilder implements OutboxCommandBuilder {

  @Override
  public String getEventType() {
    return "ORDER_CREATED";
  }

  @Override
  public Command build(OutboxEvent event, ObjectMapper mapper) {
    try {
      Order order = mapper.readValue(event.getPayload().asText(), Order.class);
      return UpdateAssetCommand.builder()
          .outboxId(event.getId())
          .eventType(getEventType())
          .order(order)
          .build();
    } catch (Exception e) {
      //TODO
      throw new RuntimeException("Failed to build command", e);
    }
  }
}
