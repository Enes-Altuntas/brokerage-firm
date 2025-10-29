package com.inghubs.asset.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.model.Command;
import com.inghubs.order.model.Order;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckAssetValidationCommand implements Command {

  private UUID outboxId;

  private Order order;

}
