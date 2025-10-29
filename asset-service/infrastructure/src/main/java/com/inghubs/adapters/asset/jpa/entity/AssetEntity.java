package com.inghubs.adapters.asset.jpa.entity;

import com.inghubs.asset.model.Asset;
import com.inghubs.common.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
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

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID customerId;

  private String assetName;

  private BigDecimal size;

  private BigDecimal usableSize;

  public AssetEntity(Asset asset) {
    this.id = asset.getId();
    this.customerId = asset.getCustomerId();
    this.assetName = asset.getAssetName();
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
        .id(id)
        .customerId(customerId)
        .assetName(assetName)
        .size(size)
        .usableSize(usableSize)
        .createdBy(getCreatedBy())
        .updatedBy(getUpdatedBy())
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .deletedAt(getDeletedAt())
        .build();
  }

}
