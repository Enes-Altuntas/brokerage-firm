package com.inghubs.adapters.asset.jpa;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import com.inghubs.adapters.asset.jpa.repository.AssetRepository;
import com.inghubs.adapters.asset.jpa.service.AssetQueryService;
import com.inghubs.adapters.asset.jpa.specification.AssetSpecification;
import com.inghubs.adapters.asset.rest.model.request.AssetFilterRequest;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.common.rest.model.PaginationRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetDataAdapter implements AssetPort, AssetQueryService {

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

  @Override
  public Page<AssetEntity> query(AssetFilterRequest filterRequest, PaginationRequest paginationRequest) {

    Pageable pageable = buildPageable(paginationRequest);

    Specification<AssetEntity> assetEntitySpecification = AssetSpecification.filterAssets(
        filterRequest);

    return assetRepository.findAll(assetEntitySpecification, pageable);
  }

  @Override
  public AssetEntity query(AssetFilterRequest filterRequest) {

    Optional<AssetEntity> entity = assetRepository.findByIdAssetNameAndIdCustomerId(
        filterRequest.getAssetName(), filterRequest.getCustomerId());

    return entity.orElse(null);
  }

  private Pageable buildPageable(PaginationRequest paginationRequest) {
    Sort.Direction direction = "DESC".equalsIgnoreCase(paginationRequest.direction())
        ? Sort.Direction.DESC : Sort.Direction.ASC;

    Sort sort = Sort.by(direction, paginationRequest.sortBy() == null ? "createdAt" : paginationRequest.sortBy());

    return PageRequest.of(paginationRequest.page(), paginationRequest.size(), sort);
  }
}
