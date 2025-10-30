package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.UpdateAssetCommand;

public interface OrderCreateAssetUpdateStrategy {

  String suffix = "OrderCreateAssetUpdateStrategy";

  void updateAsset(UpdateAssetCommand command);

}
