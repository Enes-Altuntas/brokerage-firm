package com.inghubs.asset.strategies;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class OrderCancelSellSideUpdateStrategyTest {

  @InjectMocks
  private OrderCancelSellSideUpdateStrategy strategy;

  @Mock
  private AssetPort assetPort;
  @Mock
  private TransactionTemplate transactionTemplate;
  @Mock
  private OutboxPort outboxPort;
  @Mock
  private InboxPort inboxPort;

  @Test
  void updateAsset_shouldConfirmCancel_whenAssetIsValid() {
    // Given
    UUID customerId = UUID.randomUUID();
    String assetName = "BTC";
    Order order = Order.builder()
        .customerId(customerId)
        .assetName(assetName)
        .size(BigDecimal.TEN)
        .build();
    UpdateAssetCommand command = UpdateAssetCommand.builder().order(order).build();
    Asset asset = Asset.builder().assetName(assetName).usableSize(BigDecimal.TEN).build();
    when(assetPort.retrieveCustomerAsset(assetName, customerId)).thenReturn(asset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(assetPort).updateOrSaveAsset(asset);
    verify(outboxPort).createOrderOutboxEntity(OrderCancelSellSideUpdateStrategy.ORDER_CANCEL_CONFIRMED, order);
    verify(inboxPort).createInboxEntity(command);
  }

  @Test
  void updateAsset_shouldRejectCancel_whenAssetIsInvalid() {
    // Given
    UUID customerId = UUID.randomUUID();
    String assetName = "BTC";
    Order order = Order.builder()
        .customerId(customerId)
        .assetName(assetName)
        .size(BigDecimal.TEN)
        .build();
    UpdateAssetCommand command = UpdateAssetCommand.builder().order(order).build();
    Asset asset = Asset.builder().assetName(assetName).usableSize(BigDecimal.ONE).build();

    when(assetPort.retrieveCustomerAsset(assetName, customerId)).thenReturn(asset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(outboxPort).createOrderOutboxEntity(OrderCancelSellSideUpdateStrategy.ORDER_CANCEL_REJECTED, order);
    verify(inboxPort).createInboxEntity(command);
  }
}