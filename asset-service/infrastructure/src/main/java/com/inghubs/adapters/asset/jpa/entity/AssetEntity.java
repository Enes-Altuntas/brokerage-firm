package com.inghubs.adapters.asset.jpa.entity;

import com.inghubs.asset.model.Asset;
import com.inghubs.common.jpa.entity.BaseEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assets")
public class AssetEntity extends BaseEntity {

  @EmbeddedId
  private AssetId id;

  private BigDecimal size;
  private BigDecimal usableSize;

  public AssetEntity(Asset asset) {
    this.id = new AssetId(asset.getCustomerId(), asset.getAssetName());
    this.size = asset.getSize();
    this.usableSize = asset.getUsableSize();
    setCreatedBy(asset.getCreatedBy());
    setUpdatedBy(asset.getUpdatedBy());
    setCreatedAt(asset.getCreatedAt());
    setUpdatedAt(asset.getUpdatedAt());
    setDeletedAt(asset.getDeletedAt());
  }

  public Asset toDomain() {
    return Asset.builder()
        .customerId(id.getCustomerId())
        .assetName(id.getAssetName())
        .size(size)
        .usableSize(usableSize)
        .createdBy(getCreatedBy())
        .updatedBy(getUpdatedBy())
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .deletedAt(getDeletedAt())
        .build();
  }

  @Getter
  @Embeddable
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class AssetId implements Serializable {

    private UUID customerId;
    private String assetName;
  }
}
