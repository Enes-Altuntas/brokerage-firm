package com.inghubs.asset.factory;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.asset.strategies.abstracts.OrderCancelAssetUpdateStrategy;
import com.inghubs.asset.strategies.abstracts.OrderCreateAssetUpdateStrategy;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetUpdateFactory implements AssetUpdateStrategyFactory {

  public static final String ORDER_CREATED = "ORDER_CREATED";
  public static final String ORDER_CANCEL_REQUESTED = "ORDER_CANCEL_REQUESTED";
  private final Map<String, OrderCreateAssetUpdateStrategy> orderCreateAssetUpdateStrategyMap;
  private final Map<String, OrderCancelAssetUpdateStrategy> orderCancelAssetUpdateStrategyMap;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    if(command.getEventType().equals(ORDER_CREATED)) {
      OrderCreateAssetUpdateStrategy orderCreateAssetUpdateStrategy = orderCreateAssetUpdateStrategyMap.get(
          command.getOrder().getSide().name().toUpperCase() + OrderCreateAssetUpdateStrategy.suffix);

      if (orderCreateAssetUpdateStrategy == null) {
        throw new RuntimeException();
      }

      orderCreateAssetUpdateStrategy.updateAsset(command);
    } else if(command.getEventType().equals(ORDER_CANCEL_REQUESTED)) {
      OrderCancelAssetUpdateStrategy orderCancelAssetUpdateStrategy = orderCancelAssetUpdateStrategyMap.get(
          command.getOrder().getSide().name().toUpperCase() + OrderCancelAssetUpdateStrategy.suffix);

      if (orderCancelAssetUpdateStrategy == null) {
        throw new RuntimeException();
      }

      orderCancelAssetUpdateStrategy.updateAsset(command);
    }
  }
}
