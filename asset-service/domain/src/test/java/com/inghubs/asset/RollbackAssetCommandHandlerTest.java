package com.inghubs.asset;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.factory.abstracts.CancelAssetStrategyFactory;
import com.inghubs.asset.handler.RollbackAssetCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import com.inghubs.order.model.Order;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RollbackAssetCommandHandlerTest {

  private InboxPort inboxPort;
  private LockPort lockPort;
  private CancelAssetStrategyFactory cancelAssetStrategyFactory;
  private RollbackAssetCommandHandler handler;

  @BeforeEach
  void setUp() {
    inboxPort = mock(InboxPort.class);
    lockPort = mock(LockPort.class);
    cancelAssetStrategyFactory = mock(CancelAssetStrategyFactory.class);
    handler = new RollbackAssetCommandHandler(inboxPort, lockPort, cancelAssetStrategyFactory);
  }

  @Test
  void handle_shouldNotProcessCommand_whenInboxIsNotNull() {
    // Given
    var order = Order.builder().id(UUID.randomUUID()).customerId(UUID.randomUUID()).assetName("ING").build();
    var command = RollbackAssetCommand.builder().outboxId(UUID.randomUUID()).order(order).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(mock(Inbox.class));

    // When
    handler.handle(command);

    // Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(lockPort).execute(runnableCaptor.capture(), eq(order.getCustomerId().toString() + ":" + order.getAssetName()));
    runnableCaptor.getValue().run();

    verify(cancelAssetStrategyFactory, never()).cancelAsset(command);
  }

  @Test
  void handle_shouldProcessCommand_whenInboxIsNull() {
    // Given
    var order = Order.builder().id(UUID.randomUUID()).customerId(UUID.randomUUID()).assetName("ING").build();
    var command = RollbackAssetCommand.builder().outboxId(UUID.randomUUID()).order(order).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(null);

    // When
    handler.handle(command);

    // Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(lockPort).execute(runnableCaptor.capture(), eq(order.getCustomerId() + ":" + order.getAssetName()));
    runnableCaptor.getValue().run();

    verify(cancelAssetStrategyFactory).cancelAsset(command);
  }
}
