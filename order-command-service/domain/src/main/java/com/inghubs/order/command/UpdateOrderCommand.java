package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import com.inghubs.order.model.Order;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateOrderCommand implements Command {

  private UUID outboxId;

  private String eventType;

  private Order order;

}
