package com.inghubs.asset.factory;

import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.asset.strategies.abstracts.AssetUpdateStrategy;
import com.inghubs.order.model.Order;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetUpdateFactory implements AssetUpdateStrategyFactory {

  private final Map<String, AssetUpdateStrategy> assetValidations;

  @Override
  public void checkValidationAndUpdateAsset(UUID outboxId, Order order) {

    AssetUpdateStrategy assetupdateStrategy = assetValidations.get(
        order.getSide().name().toUpperCase() + AssetUpdateStrategy.suffix);

    if (assetupdateStrategy == null) {
      throw new RuntimeException();
    }

    assetupdateStrategy.checkValidationAndUpdateAsset(outboxId, order);
  }
}
