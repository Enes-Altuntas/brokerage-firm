package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.UpdateAssetCommand;

public interface OrderCancelAssetUpdateStrategy {

  String suffix = "OrderCancelAssetUpdateStrategy";

  void updateAsset(UpdateAssetCommand command);

}
