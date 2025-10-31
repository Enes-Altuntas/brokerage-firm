package com.inghubs.asset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.CancelBuySideStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

class CancelBuySideStrategyTest {

  private AssetPort assetPort;
  private TransactionTemplate transactionTemplate;
  private OutboxPort outboxPort;
  private InboxPort inboxPort;
  private CancelBuySideStrategy strategy;
  private Asset tryAsset;

  @BeforeEach
  void setUp() {
    assetPort = mock(AssetPort.class);
    transactionTemplate = mock(TransactionTemplate.class);
    outboxPort = mock(OutboxPort.class);
    inboxPort = mock(InboxPort.class);
    strategy = new CancelBuySideStrategy(assetPort, transactionTemplate, outboxPort, inboxPort);
    tryAsset = Asset.builder().usableSize(BigDecimal.valueOf(100)).build();
  }

  @Test
  void cancelAsset_shouldConfirmCancel_whenAssetIsValid() {
    // Given
    var order = Order.builder()
        .id(UUID.randomUUID())
        .customerId(UUID.randomUUID())
        .size(BigDecimal.TEN)
        .price(BigDecimal.ONE)
        .build();
    var command = RollbackAssetCommand.builder()
        .order(order)
        .outboxId(UUID.randomUUID())
        .eventType("ORDER_CANCEL_REQUESTED")
        .build();

    when(assetPort.retrieveCustomerAsset("TRY", order.getCustomerId())).thenReturn(tryAsset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.cancelAsset(command);

    // Then
    verify(assetPort).createOrUpdateAsset(tryAsset);
    verify(outboxPort).createOrderOutboxEntity(eq("ORDER_CANCEL_CONFIRMED"), any(), any());
    verify(inboxPort).createInboxEntity(any(), any(), any(), any());
  }

  @Test
  void cancelAsset_shouldRejectCancel_whenAssetIsInvalid() {
    // Given
    var order = Order.builder()
        .id(UUID.randomUUID())
        .customerId(UUID.randomUUID())
        .size(BigDecimal.TEN)
        .price(BigDecimal.TEN)
        .build();
    var command = RollbackAssetCommand.builder()
        .order(order)
        .outboxId(UUID.randomUUID())
        .eventType("ORDER_CANCEL_REQUESTED")
        .build();
    tryAsset.setUsableSize(BigDecimal.ONE);

    when(assetPort.retrieveCustomerAsset("TRY", order.getCustomerId())).thenReturn(tryAsset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.cancelAsset(command);

    // Then
    verify(outboxPort).createOrderOutboxEntity(eq("ORDER_CANCEL_REJECTED"), any(), any());
    verify(inboxPort).createInboxEntity(any(), any(), any(), any());
  }
}