package com.inghubs.adapters.outbox.jpa.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.common.jpa.entity.BaseEntity;
import com.inghubs.common.util.JsonNodeConverterUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
  @Convert(converter = JsonNodeConverterUtil.class)
  @Column(columnDefinition = "jsonb")
  private JsonNode payload;

  @Column(name = "processed_at")
  private Instant processedAt;

  @Column(name = "processed", nullable = false)
  @Builder.Default
  private Boolean isProcessed = false;

  @Column(name = "request_id")
  private String requestId;

}