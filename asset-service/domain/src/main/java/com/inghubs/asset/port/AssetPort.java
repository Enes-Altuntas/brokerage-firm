package com.inghubs.asset.port;

import com.inghubs.asset.model.Asset;
import java.util.UUID;

public interface AssetPort {

  Asset retrieveCustomerAsset(UUID id, UUID customerId);

  Asset retrieveCustomerTRYAsset(UUID customerId);

  Asset updateOrSaveAsset(Asset asset);

}
