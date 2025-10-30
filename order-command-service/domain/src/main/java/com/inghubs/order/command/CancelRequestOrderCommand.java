package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CancelRequestOrderCommand implements Command {

  private UUID orderId;

  private UUID customerId;

}