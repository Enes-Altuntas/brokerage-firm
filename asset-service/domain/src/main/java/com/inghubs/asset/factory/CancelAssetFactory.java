package com.inghubs.asset.factory;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.factory.abstracts.CancelAssetStrategyFactory;
import com.inghubs.asset.strategies.abstracts.CancelAssetStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelAssetFactory implements CancelAssetStrategyFactory {

  private final Map<String, CancelAssetStrategy> cancelAssetStrategyMap;

  @Override
  public void cancelAsset(RollbackAssetCommand command) {
    CancelAssetStrategy cancelAssetStrategy = cancelAssetStrategyMap.get(
        command.getOrder().getSide().name().toUpperCase() + CancelAssetStrategy.suffix);

    if (cancelAssetStrategy == null) {
      throw new RuntimeException();
    }

    cancelAssetStrategy.cancelAsset(command);
  }
}
