package com.inghubs.adapters.inbox.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.jpa.entity.BaseEntity;
import com.inghubs.inbox.model.Inbox;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inbox")
public class InboxEntity extends BaseEntity {

  @Id
  private UUID id;

  @Column(name = "aggregate_id", nullable = false)
  private UUID aggregateId;

  @Column(name = "aggregate_type", nullable = false)
  private String aggregateType;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb", updatable = false)
  private JsonNode payload;

  @Column(name = "processed_at")
  private Instant processedAt;

  @Column(name = "processed", nullable = false)
  private Boolean isProcessed;

  @Column(name = "request_id")
  private String requestId;

  public InboxEntity(Inbox inbox) {
    this.id = inbox.getId();
    this.aggregateId = inbox.getAggregateId();
    this.aggregateType = inbox.getAggregateType();
    this.eventType = inbox.getEventType();
    this.payload = inbox.getPayload();
    this.processedAt = inbox.getProcessedAt();
    this.isProcessed = inbox.getIsProcessed();
    this.requestId = inbox.getRequestId();
    this.setCreatedAt(inbox.getCreatedAt());
    this.setUpdatedAt(inbox.getUpdatedAt());
    this.setDeletedAt(inbox.getDeletedAt());
    this.setCreatedBy(inbox.getCreatedBy());
    this.setUpdatedBy(inbox.getUpdatedBy());
  }

  public Inbox toDomain() {
    return Inbox.builder()
        .id(id)
        .aggregateId(aggregateId)
        .aggregateType(aggregateType)
        .eventType(eventType)
        .payload(payload)
        .processedAt(processedAt)
        .isProcessed(isProcessed)
        .requestId(requestId)
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .deletedAt(getDeletedAt())
        .createdBy(getCreatedBy())
        .updatedBy(getUpdatedBy())
        .build();
  }
}