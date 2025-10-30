package com.inghubs.adapters.asset.jpa;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import com.inghubs.adapters.asset.jpa.repository.AssetRepository;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetDataAdapter implements AssetPort {

  public static final String TRY = "TRY";
  private final AssetRepository assetRepository;

  @Override
  public Asset retrieveCustomerAsset(String assetName, UUID customerId) {
    Optional<AssetEntity> entity = assetRepository.findByIdAssetNameAndIdCustomerId(assetName, customerId);

    if(entity.isEmpty()) {
      return null;
    }

    return entity.map(AssetEntity::toDomain).orElse(null);
  }

  @Override
  public Asset updateOrSaveAsset(Asset asset) {
    AssetEntity entity = new AssetEntity(asset);

    AssetEntity savedEntity = assetRepository.save(entity);

    return savedEntity.toDomain();
  }
}
