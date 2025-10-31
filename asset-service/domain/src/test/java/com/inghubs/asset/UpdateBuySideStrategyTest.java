package com.inghubs.asset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.UpdateBuySideStrategy;
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

class UpdateBuySideStrategyTest {

  private AssetPort assetPort;
  private TransactionTemplate transactionTemplate;
  private OutboxPort outboxPort;
  private InboxPort inboxPort;
  private UpdateBuySideStrategy strategy;
  private Asset tryAsset;

  @BeforeEach
  void setUp() {
    assetPort = mock(AssetPort.class);
    transactionTemplate = mock(TransactionTemplate.class);
    outboxPort = mock(OutboxPort.class);
    inboxPort = mock(InboxPort.class);
    strategy = new UpdateBuySideStrategy(assetPort, transactionTemplate, outboxPort, inboxPort);
    tryAsset = Asset.builder().usableSize(BigDecimal.valueOf(100)).build();
  }

  @Test
  void updateAsset_shouldValidateOrder_whenAssetIsValid() {
    // Given
    var order = Order.builder()
        .id(UUID.randomUUID())
        .customerId(UUID.randomUUID())
        .size(BigDecimal.TEN)
        .price(BigDecimal.ONE)
        .build();
    var command = UpdateAssetCommand.builder()
        .order(order)
        .outboxId(UUID.randomUUID())
        .eventType("ORDER_CREATED")
        .build();

    when(assetPort.retrieveCustomerAsset("TRY", order.getCustomerId())).thenReturn(tryAsset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(assetPort).createOrUpdateAsset(tryAsset);
    verify(outboxPort).createOrderOutboxEntity(eq("ORDER_VALIDATED"), any(), any());
    verify(inboxPort).createInboxEntity(any(), any(), any(), any());
  }

  @Test
  void updateAsset_shouldRejectOrder_whenAssetIsInvalid() {
    // Given
    var order = Order.builder()
        .id(UUID.randomUUID())
        .customerId(UUID.randomUUID())
        .size(BigDecimal.TEN)
        .price(BigDecimal.TEN)
        .build();
    var command = UpdateAssetCommand.builder()
        .order(order)
        .outboxId(UUID.randomUUID())
        .eventType("ORDER_CREATED")
        .build();
    tryAsset.setUsableSize(BigDecimal.ONE);

    when(assetPort.retrieveCustomerAsset("TRY", order.getCustomerId())).thenReturn(tryAsset);
    doAnswer(invocation -> {
      Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(null);
      return null;
    }).when(transactionTemplate).executeWithoutResult(any());

    // When
    strategy.updateAsset(command);

    // Then
    verify(outboxPort).createOrderOutboxEntity(eq("ORDER_REJECTED"), any(), any());
    verify(inboxPort).createInboxEntity(any(), any(), any(), any());
  }
}