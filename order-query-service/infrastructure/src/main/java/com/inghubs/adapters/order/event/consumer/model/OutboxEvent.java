package com.inghubs.adapters.order.event.consumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.order.command.CreateOrderCommand;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

  private UUID id;

  @JsonProperty("aggregate_id")
  private UUID aggregateId;

  @JsonProperty("aggregate_type")
  private String aggregateType;

  @JsonProperty("event_type")
  private String eventType;

  @JsonProperty("payload")
  private JsonNode payload;

  @JsonProperty("processed_at")
  private Instant processedAt;

  @JsonProperty("processed")
  private Boolean isProcessed;

  @JsonProperty("request_id")
  private String requestId;

  @JsonProperty("created_by")
  private String createdBy;

  @JsonProperty("updated_by")
  private String updatedBy;

  @JsonProperty("created_at")
  private Instant createdAt;

  @JsonProperty("updated_at")
  private Instant updatedAt;

  @JsonProperty("deleted_at")
  private Instant deletedAt;

  public CreateOrderCommand toCommand() {
    return CreateOrderCommand.builder()
        .id(id)
        .aggregateId(aggregateId)
        .aggregateType(aggregateType)
        .eventType(eventType)
        .payload(payload)
        .processedAt(processedAt)
        .isProcessed(isProcessed)
        .requestId(requestId)
        .createdBy(createdBy)
        .updatedBy(updatedBy)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .deletedAt(deletedAt)
        .build();
  }

}
