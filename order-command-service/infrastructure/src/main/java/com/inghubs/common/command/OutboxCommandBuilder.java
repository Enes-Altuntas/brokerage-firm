package com.inghubs.common.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.event.consumer.model.OutboxEvent;
import com.inghubs.common.model.Command;

public interface OutboxCommandBuilder {

  String getEventType();
  Command build(OutboxEvent event, ObjectMapper mapper);

}
