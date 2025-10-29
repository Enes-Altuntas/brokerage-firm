package com.inghubs.order.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.model.Command;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrderCommand implements Command {

  private UUID id;

  private UUID aggregateId;

  private String aggregateType;

  private String eventType;

  private JsonNode payload;

  private Instant processedAt;

  private Boolean isProcessed;

  private String requestId;

  private String createdBy;

  private String updatedBy;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;
}
