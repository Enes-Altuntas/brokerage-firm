package com.inghubs.asset.factory.abstracts;

import com.inghubs.order.model.Order;
import java.util.UUID;

public interface AssetUpdateStrategyFactory {

  void checkValidationAndUpdateAsset(UUID outboxId, Order order);

}
