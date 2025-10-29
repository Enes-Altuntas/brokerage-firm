package com.inghubs.adapters.outbox.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.jpa.entity.BaseEntity;
import com.inghubs.outbox.model.Outbox;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "outbox")
public class OutboxEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
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

  public static OutboxEntity toEntity(Outbox outbox) {
    return OutboxEntity.builder()
        .id(outbox.getId())
        .aggregateId(outbox.getAggregateId())
        .aggregateType(outbox.getAggregateType())
        .eventType(outbox.getEventType())
        .payload(outbox.getPayload())
        .createdAt(outbox.getCreatedAt())
        .updatedAt(outbox.getUpdatedAt())
        .deletedAt(outbox.getDeletedAt())
        .createdBy(outbox.getCreatedBy())
        .updatedBy(outbox.getUpdatedBy())
        .build();
  }
}