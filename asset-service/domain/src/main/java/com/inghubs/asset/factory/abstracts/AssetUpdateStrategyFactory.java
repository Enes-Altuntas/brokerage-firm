package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.UpdateAssetCommand;

public interface AssetUpdateStrategyFactory {

  void updateAsset(UpdateAssetCommand command);

}
