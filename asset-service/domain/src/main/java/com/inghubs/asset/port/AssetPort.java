package com.inghubs.asset.port;

import com.inghubs.asset.model.Asset;
import java.util.UUID;

public interface AssetPort {

  Asset retrieveCustomerAsset(String assetName, UUID customerId);

  Asset updateOrSaveAsset(Asset asset);

}
