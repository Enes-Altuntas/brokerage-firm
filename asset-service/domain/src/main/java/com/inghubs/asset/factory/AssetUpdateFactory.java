package com.inghubs.asset.factory;

import com.inghubs.asset.command.CheckAssetValidationCommand;
import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.asset.strategies.abstracts.AssetUpdateStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetUpdateFactory implements AssetUpdateStrategyFactory {

  private final Map<String, AssetUpdateStrategy> assetValidations;

  @Override
  public void checkValidationAndUpdateAsset(CheckAssetValidationCommand command) {

    AssetUpdateStrategy assetupdateStrategy = assetValidations.get(
        command.getOrder().getSide().name().toUpperCase() + AssetUpdateStrategy.suffix);

    if (assetupdateStrategy == null) {
      throw new RuntimeException();
    }

    assetupdateStrategy.checkValidationAndUpdateAsset(command);
  }
}
