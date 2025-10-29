package com.inghubs.asset.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.model.Command;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckAssetValidationCommand implements Command {

  private UUID outboxId;

  private UUID aggregateId;

  private String aggregateType;

  private String eventType;

  private JsonNode payload;

  private String createdBy;

  private String updatedBy;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

}
