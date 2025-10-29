package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.CheckAssetValidationCommand;

public interface AssetUpdateStrategy {

  String suffix = "AssetUpdateStrategy";

  void checkValidationAndUpdateAsset(CheckAssetValidationCommand command);

}
