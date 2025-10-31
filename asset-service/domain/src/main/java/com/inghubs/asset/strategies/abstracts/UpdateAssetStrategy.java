package com.inghubs.asset.strategies.abstracts;

import com.inghubs.asset.command.UpdateAssetCommand;

public interface UpdateAssetStrategy {

  String suffix = "OrderCreateAssetUpdateStrategy";

  void updateAsset(UpdateAssetCommand command);

}
