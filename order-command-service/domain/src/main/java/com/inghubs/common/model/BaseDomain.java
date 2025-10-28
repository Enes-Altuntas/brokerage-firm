package com.inghubs.common.model;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BaseDomain {

  private Instant createdAt;

  private Instant updatedAt;

  private Instant deletedAt;

  private String createdBy;

  private String updatedBy;

}
