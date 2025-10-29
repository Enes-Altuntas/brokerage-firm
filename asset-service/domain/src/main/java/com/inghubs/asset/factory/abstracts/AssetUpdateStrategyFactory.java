package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.CheckValidationAndUpdateAssetCommand;

public interface AssetUpdateStrategyFactory {

  void checkValidationAndUpdateAsset(CheckValidationAndUpdateAssetCommand command);

}
