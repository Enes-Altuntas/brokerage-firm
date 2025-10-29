package com.inghubs.outbox.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Outbox {

  private UUID id;

  private UUID aggregateId;

  private String aggregateType;

  private String eventType;

  private JsonNode payload;

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  private String createdBy;

  private String updatedBy;

}
