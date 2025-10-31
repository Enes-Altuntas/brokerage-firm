package com.inghubs.asset;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.MatchOrderCommand;
import com.inghubs.asset.exception.AssetBusinessException;
import com.inghubs.asset.handler.MatchOrderCommandHandler;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import com.inghubs.order.model.MatchOrder;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

class MatchOrderCommandHandlerTest {

  private InboxPort inboxPort;
  private AssetPort assetPort;
  private LockPort lockPort;
  private TransactionTemplate transactionTemplate;
  private OutboxPort outboxPort;
  private MatchOrderCommandHandler handler;

  @BeforeEach
  void setUp() {
    inboxPort = mock(InboxPort.class);
    assetPort = mock(AssetPort.class);
    lockPort = mock(LockPort.class);
    transactionTemplate = mock(TransactionTemplate.class);
    outboxPort = mock(OutboxPort.class);
    handler = new MatchOrderCommandHandler(inboxPort, assetPort, lockPort, transactionTemplate, outboxPort);
  }

  @Test
  void handle_shouldNotProcessCommand_whenInboxIsNotNull() {
    // Given
    var matchOrder = MatchOrder.builder()
        .buyOrderId(UUID.randomUUID())
        .sellOrderId(UUID.randomUUID())
        .assetName("ING")
        .build();
    var command = MatchOrderCommand.builder().outboxId(UUID.randomUUID()).matchOrder(matchOrder).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(mock(Inbox.class));

    // When
    handler.handle(command);

    // Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(lockPort).execute(runnableCaptor.capture(), anyString(), anyString());
    runnableCaptor.getValue().run();

    verify(assetPort, never()).retrieveCustomerAsset(anyString(), any(UUID.class));
  }

  @Test
  void handle_shouldThrowException_whenBuyerTRYAssetNotFound() {
    // Given
    var matchOrder = MatchOrder.builder()
        .buyOrderId(UUID.randomUUID())
        .sellOrderId(UUID.randomUUID())
        .assetName("ING")
        .buyerCustomerId(UUID.randomUUID())
        .build();
    var command = MatchOrderCommand.builder().outboxId(UUID.randomUUID()).matchOrder(matchOrder).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(null);
    when(assetPort.retrieveCustomerAsset("TRY", matchOrder.getBuyerCustomerId())).thenReturn(null);

    // When & Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    doAnswer(invocation -> {
      runnableCaptor.getValue().run();
      return null;
    }).when(lockPort).execute(runnableCaptor.capture(), anyString(), anyString());

    assertThrows(AssetBusinessException.class, () -> handler.handle(command));
  }
}
