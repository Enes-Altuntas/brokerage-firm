package com.inghubs.adapters.asset.jpa.repository;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssetRepository extends JpaRepository<AssetEntity, AssetEntity.AssetId>,
    JpaSpecificationExecutor<AssetEntity> {

  Optional<AssetEntity> findByIdAssetNameAndIdCustomerId(String assetName, UUID customerId);

}
