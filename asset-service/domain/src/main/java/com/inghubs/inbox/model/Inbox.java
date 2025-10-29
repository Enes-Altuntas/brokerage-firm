package com.inghubs.inbox.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.model.BaseDomain;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Inbox extends BaseDomain {

  private UUID id;

  private UUID aggregateId;

  private String aggregateType;

  private String eventType;

  private JsonNode payload;

  private Instant processedAt;

  private Boolean isProcessed;

  private String requestId;

}
