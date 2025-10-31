package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import com.inghubs.order.model.Order;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CancelOrderCommand implements Command {

  private UUID outboxId;

  private String eventType;

  private Order order;

}
