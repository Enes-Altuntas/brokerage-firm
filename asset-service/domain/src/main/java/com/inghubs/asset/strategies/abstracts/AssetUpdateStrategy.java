package com.inghubs.asset.strategies.abstracts;

import com.inghubs.order.model.Order;
import java.util.UUID;

public interface AssetUpdateStrategy {

  String suffix = "AssetUpdateStrategy";

  void checkValidationAndUpdateAsset(UUID outboxId, Order order);

}
