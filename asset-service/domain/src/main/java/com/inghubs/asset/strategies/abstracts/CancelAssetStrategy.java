package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.RollbackAssetCommand;

public interface CancelAssetStrategy {

  String suffix = "OrderCancelAssetUpdateStrategy";

  void cancelAsset(RollbackAssetCommand command);

}
