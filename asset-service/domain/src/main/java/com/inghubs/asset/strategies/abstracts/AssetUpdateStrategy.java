package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.CheckValidationAndUpdateAssetCommand;

public interface AssetUpdateStrategy {

  String suffix = "AssetUpdateStrategy";

  void checkValidationAndUpdateAsset(CheckValidationAndUpdateAssetCommand command);

}
