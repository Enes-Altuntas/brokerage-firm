package com.inghubs.asset;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.UpdateAssetFactory;
import com.inghubs.asset.strategies.abstracts.UpdateAssetStrategy;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateAssetFactoryTest {

  private UpdateAssetFactory factory;
  private UpdateAssetStrategy buyStrategy;
  private UpdateAssetStrategy sellStrategy;

  @BeforeEach
  void setUp() {
    buyStrategy = mock(UpdateAssetStrategy.class);
    sellStrategy = mock(UpdateAssetStrategy.class);
    factory = new UpdateAssetFactory(Map.of(
        "BUY" + UpdateAssetStrategy.suffix, buyStrategy,
        "SELL" + UpdateAssetStrategy.suffix, sellStrategy
    ));
  }

  @Test
  void updateAsset_shouldCallBuyStrategy_whenOrderSideIsBuy() {
    // Given
    var order = Order.builder().side(OrderSide.BUY).build();
    var command = UpdateAssetCommand.builder().order(order).build();

    // When
    factory.updateAsset(command);

    // Then
    verify(buyStrategy).updateAsset(command);
  }

  @Test
  void updateAsset_shouldCallSellStrategy_whenOrderSideIsSell() {
    // Given
    var order = Order.builder().side(OrderSide.SELL).build();
    var command = UpdateAssetCommand.builder().order(order).build();

    // When
    factory.updateAsset(command);

    // Then
    verify(sellStrategy).updateAsset(command);
  }

  @Test
  void updateAsset_shouldThrowException_whenStrategyNotFound() {
    // Given
    var factory = new UpdateAssetFactory(Map.of());
    var order = Order.builder().side(OrderSide.BUY).build();
    var command = UpdateAssetCommand.builder().order(order).build();

    // When & Then
    assertThrows(RuntimeException.class, () -> factory.updateAsset(command));
  }
}
