package com.inghubs.asset.command;

import com.inghubs.common.model.Command;
import com.inghubs.order.model.MatchOrder;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchOrderCommand implements Command {

  private UUID outboxId;

  private UUID aggregateId;

  private MatchOrder matchOrder;

}
