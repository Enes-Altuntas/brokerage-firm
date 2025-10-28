package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import java.util.UUID;
import lombok.Builder;

@Builder
public class CancelOrderCommand implements Command {

  private UUID orderId;

  private UUID customerId;

}
