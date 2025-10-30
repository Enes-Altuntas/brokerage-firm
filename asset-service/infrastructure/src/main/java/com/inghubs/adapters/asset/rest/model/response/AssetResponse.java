package com.inghubs.adapters.asset.rest.model.response;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AssetResponse(

    UUID customerId,

    String assetName,

    BigDecimal size,

    BigDecimal usableSize,

    Instant createdAt,

    Instant updatedAt

) {

  public static AssetResponse toResponse(AssetEntity asset) {
    if (asset == null) {
      return null;
    }
    return AssetResponse.builder()
        .customerId(asset.getId().getCustomerId())
        .assetName(asset.getId().getAssetName())
        .size(asset.getSize())
        .usableSize(asset.getUsableSize())
        .createdAt(asset.getCreatedAt())
        .updatedAt(asset.getUpdatedAt())
        .build();
  }
}
