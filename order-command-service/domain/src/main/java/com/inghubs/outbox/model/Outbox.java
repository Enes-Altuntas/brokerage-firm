package com.inghubs.outbox.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.model.BaseDomain;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Outbox extends BaseDomain {

  private UUID id;

  private UUID aggregateId;

  private String aggregateType;

  private String eventType;

  private JsonNode payload;

}
