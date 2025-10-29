package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.CheckAssetValidationCommand;

public interface AssetUpdateStrategyFactory {

  void checkValidationAndUpdateAsset(CheckAssetValidationCommand command);

}
