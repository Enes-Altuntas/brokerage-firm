package com.inghubs.asset.factory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.strategies.OrderCancelBuySideUpdateStrategy;
import com.inghubs.asset.strategies.OrderCancelSellSideUpdateStrategy;
import com.inghubs.asset.strategies.OrderCreateBuySideUpdateStrategy;
import com.inghubs.asset.strategies.OrderCreateSellSideUpdateStrategy;
import com.inghubs.asset.strategies.abstracts.OrderCancelAssetUpdateStrategy;
import com.inghubs.asset.strategies.abstracts.OrderCreateAssetUpdateStrategy;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetUpdateFactoryTest {

  private AssetUpdateFactory assetUpdateFactory;

  @Mock
  private Map<String, OrderCreateAssetUpdateStrategy> orderCreateAssetUpdateStrategyMap;

  @Mock
  private Map<String, OrderCancelAssetUpdateStrategy> orderCancelAssetUpdateStrategyMap;

  @Test
  void updateAsset_shouldCallCreateBuyStrategy_whenOrderCreatedAndSideIsBuy() {
    // Given
    Order order = Order.builder().side(OrderSide.BUY).build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .eventType(AssetUpdateFactory.ORDER_CREATED)
        .order(order)
        .build();

    OrderCreateBuySideUpdateStrategy buyStrategy = mock(OrderCreateBuySideUpdateStrategy.class);
    Map<String, OrderCreateAssetUpdateStrategy> createStrategies = new HashMap<>();
    createStrategies.put("BUY" + OrderCreateAssetUpdateStrategy.suffix, buyStrategy);

    assetUpdateFactory = new AssetUpdateFactory(createStrategies, orderCancelAssetUpdateStrategyMap);

    // When
    assetUpdateFactory.updateAsset(command);

    // Then
    verify(buyStrategy).updateAsset(command);
  }

  @Test
  void updateAsset_shouldCallCreateSellStrategy_whenOrderCreatedAndSideIsSell() {
    // Given
    Order order = Order.builder().side(OrderSide.SELL).build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .eventType(AssetUpdateFactory.ORDER_CREATED)
        .order(order)
        .build();

    OrderCreateSellSideUpdateStrategy sellStrategy = mock(OrderCreateSellSideUpdateStrategy.class);
    Map<String, OrderCreateAssetUpdateStrategy> createStrategies = new HashMap<>();
    createStrategies.put("SELL" + OrderCreateAssetUpdateStrategy.suffix, sellStrategy);

    assetUpdateFactory = new AssetUpdateFactory(createStrategies, orderCancelAssetUpdateStrategyMap);

    // When
    assetUpdateFactory.updateAsset(command);

    // Then
    verify(sellStrategy).updateAsset(command);
  }

  @Test
  void updateAsset_shouldCallCancelBuyStrategy_whenOrderCanceledAndSideIsBuy() {
    // Given
    Order order = Order.builder().side(OrderSide.BUY).build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .eventType(AssetUpdateFactory.ORDER_CANCEL_REQUESTED)
        .order(order)
        .build();

    OrderCancelBuySideUpdateStrategy buyStrategy = mock(OrderCancelBuySideUpdateStrategy.class);
    Map<String, OrderCancelAssetUpdateStrategy> cancelStrategies = new HashMap<>();
    cancelStrategies.put("BUY" + OrderCancelAssetUpdateStrategy.suffix, buyStrategy);

    assetUpdateFactory = new AssetUpdateFactory(orderCreateAssetUpdateStrategyMap, cancelStrategies);

    // When
    assetUpdateFactory.updateAsset(command);

    // Then
    verify(buyStrategy).updateAsset(command);
  }

  @Test
  void updateAsset_shouldCallCancelSellStrategy_whenOrderCanceledAndSideIsSell() {
    // Given
    Order order = Order.builder().side(OrderSide.SELL).build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .eventType(AssetUpdateFactory.ORDER_CANCEL_REQUESTED)
        .order(order)
        .build();

    OrderCancelSellSideUpdateStrategy sellStrategy = mock(OrderCancelSellSideUpdateStrategy.class);
    Map<String, OrderCancelAssetUpdateStrategy> cancelStrategies = new HashMap<>();
    cancelStrategies.put("SELL" + OrderCancelAssetUpdateStrategy.suffix, sellStrategy);

    assetUpdateFactory = new AssetUpdateFactory(orderCreateAssetUpdateStrategyMap, cancelStrategies);

    // When
    assetUpdateFactory.updateAsset(command);

    // Then
    verify(sellStrategy).updateAsset(command);
  }
}
