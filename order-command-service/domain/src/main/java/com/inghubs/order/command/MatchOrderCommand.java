package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import com.inghubs.order.model.MatchOrder;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class MatchOrderCommand implements Command {

  private UUID outboxId;

  private String eventType;

  private MatchOrder matchOrder;

}
