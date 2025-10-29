package com.inghubs.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@ToString
@SuperBuilder
@RequiredArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public class BaseEntity {

  @CreationTimestamp
  @Column(updatable = false)
  @JsonSerialize(using = InstantSerializer.class)
  private Instant createdAt = Instant.now();

  @UpdateTimestamp
  @JsonSerialize(using = InstantSerializer.class)
  private Instant updatedAt = Instant.now();

  @JsonSerialize(using = InstantSerializer.class)
  private Instant deletedAt;

  private String createdBy;
  private String updatedBy;
}
