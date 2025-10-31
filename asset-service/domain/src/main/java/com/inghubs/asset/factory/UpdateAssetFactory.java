package com.inghubs.asset.factory;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.abstracts.UpdateAssetStrategyFactory;
import com.inghubs.asset.strategies.abstracts.UpdateAssetStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateAssetFactory implements UpdateAssetStrategyFactory {

  private final Map<String, UpdateAssetStrategy> updateAssetStrategyMap;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    UpdateAssetStrategy updateAssetStrategy = updateAssetStrategyMap.get(
        command.getOrder().getSide().name().toUpperCase() + UpdateAssetStrategy.suffix);

    if (updateAssetStrategy == null) {
      throw new RuntimeException();
    }

    updateAssetStrategy.updateAsset(command);
  }
}
