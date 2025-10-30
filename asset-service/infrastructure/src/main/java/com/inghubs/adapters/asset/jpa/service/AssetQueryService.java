package com.inghubs.adapters.asset.jpa.service;

import com.inghubs.adapters.asset.jpa.entity.AssetEntity;
import com.inghubs.adapters.asset.rest.model.request.AssetFilterRequest;
import com.inghubs.common.rest.model.PaginationRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface AssetQueryService {

  Page<AssetEntity> query(AssetFilterRequest filterRequest, PaginationRequest pageable);

  AssetEntity query(String assetName, UUID customerId);

}
