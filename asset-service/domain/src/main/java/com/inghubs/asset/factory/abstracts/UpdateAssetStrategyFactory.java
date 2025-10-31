package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.UpdateAssetCommand;

public interface UpdateAssetStrategyFactory {

  void updateAsset(UpdateAssetCommand command);

}
