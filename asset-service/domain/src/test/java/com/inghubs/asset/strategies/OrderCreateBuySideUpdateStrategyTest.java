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
class OrderCreateBuySideUpdateStrategyTest {

  @InjectMocks
  private OrderCreateBuySideUpdateStrategy strategy;

  @Mock
  private AssetPort assetPort;

  @Mock
  private TransactionTemplate transactionTemplate;

  @Mock
  private OutboxPort outboxPort;

  @Mock
  private InboxPort inboxPort;

  @Test
  void updateAsset_shouldValidateOrder_whenAssetIsValid() {
    // Given
    UUID customerId = UUID.randomUUID();
    Order order = Order.builder()
        .customerId(customerId)
        .size(BigDecimal.TEN)
        .price(BigDecimal.ONE)
        .build();
    UpdateAssetCommand command = UpdateAssetCommand.builder().order(order).build();
    Asset asset = Asset.builder().usableSize(BigDecimal.TEN).build();
    when(assetPort.retrieveCustomerAsset("TRY", customerId)).thenReturn(asset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(assetPort).updateOrSaveAsset(asset);
    verify(outboxPort).createOrderOutboxEntity(OrderCreateBuySideUpdateStrategy.ORDER_VALIDATED, order);
    verify(inboxPort).createInboxEntity(command);
  }

  @Test
  void updateAsset_shouldRejectOrder_whenAssetIsInvalid() {
    // Given
    UUID customerId = UUID.randomUUID();
    Order order = Order.builder()
        .customerId(customerId)
        .size(BigDecimal.TEN)
        .price(BigDecimal.ONE)
        .build();
    UpdateAssetCommand command = UpdateAssetCommand.builder().order(order).build();
    Asset asset = Asset.builder().usableSize(BigDecimal.ONE).build();

    when(assetPort.retrieveCustomerAsset("TRY", customerId)).thenReturn(asset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(outboxPort).createOrderOutboxEntity(OrderCreateBuySideUpdateStrategy.ORDER_REJECTED, order);
    verify(inboxPort).createInboxEntity(command);
  }
}