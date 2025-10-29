package com.inghubs.adapters.asset.jpa.repository;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<AssetEntity, UUID> {

  Optional<AssetEntity> findByIdAndCustomerId(UUID id, UUID customerId);

  Optional<AssetEntity> findByAssetNameAndCustomerId(String assetName, UUID customerId);

}
