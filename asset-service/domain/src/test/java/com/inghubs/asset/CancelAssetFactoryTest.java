package com.inghubs.asset;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.factory.CancelAssetFactory;
import com.inghubs.asset.strategies.abstracts.CancelAssetStrategy;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CancelAssetFactoryTest {

  private CancelAssetFactory factory;
  private CancelAssetStrategy buyStrategy;
  private CancelAssetStrategy sellStrategy;

  @BeforeEach
  void setUp() {
    buyStrategy = mock(CancelAssetStrategy.class);
    sellStrategy = mock(CancelAssetStrategy.class);
    factory = new CancelAssetFactory(Map.of(
        "BUY" + CancelAssetStrategy.suffix, buyStrategy,
        "SELL" + CancelAssetStrategy.suffix, sellStrategy
    ));
  }

  @Test
  void cancelAsset_shouldCallBuyStrategy_whenOrderSideIsBuy() {
    // Given
    var order = Order.builder().side(OrderSide.BUY).build();
    var command = RollbackAssetCommand.builder().order(order).build();

    // When
    factory.cancelAsset(command);

    // Then
    verify(buyStrategy).cancelAsset(command);
  }

  @Test
  void cancelAsset_shouldCallSellStrategy_whenOrderSideIsSell() {
    // Given
    var order = Order.builder().side(OrderSide.SELL).build();
    var command = RollbackAssetCommand.builder().order(order).build();

    // When
    factory.cancelAsset(command);

    // Then
    verify(sellStrategy).cancelAsset(command);
  }

  @Test
  void cancelAsset_shouldThrowException_whenStrategyNotFound() {
    // Given
    var factory = new CancelAssetFactory(Map.of());
    var order = Order.builder().side(OrderSide.BUY).build();
    var command = RollbackAssetCommand.builder().order(order).build();

    // When & Then
    assertThrows(RuntimeException.class, () -> factory.cancelAsset(command));
  }
}
