package com.inghubs.common.model;

import com.inghubs.order.model.Order;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Event {

  private UUID outboxId;

  private String eventType;

  private Order order;

}
