package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.RollbackAssetCommand;

public interface CancelAssetStrategyFactory {

  void cancelAsset(RollbackAssetCommand command);

}
