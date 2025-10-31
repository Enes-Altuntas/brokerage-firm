package com.inghubs.asset;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.abstracts.UpdateAssetStrategyFactory;
import com.inghubs.asset.handler.UpdateAssetCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import com.inghubs.order.model.Order;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateAssetCommandHandlerTest {

  private InboxPort inboxPort;
  private LockPort lockPort;
  private UpdateAssetStrategyFactory updateAssetStrategyFactory;
  private UpdateAssetCommandHandler handler;

  @BeforeEach
  void setUp() {
    inboxPort = mock(InboxPort.class);
    lockPort = mock(LockPort.class);
    updateAssetStrategyFactory = mock(UpdateAssetStrategyFactory.class);
    handler = new UpdateAssetCommandHandler(inboxPort, lockPort, updateAssetStrategyFactory);
  }

  @Test
  void handle_shouldNotProcessCommand_whenInboxIsNotNull() {
    // Given
    var order = Order.builder().id(UUID.randomUUID()).customerId(UUID.randomUUID()).assetName("ING").build();
    var command = UpdateAssetCommand.builder().outboxId(UUID.randomUUID()).order(order).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(mock(Inbox.class));

    // When
    handler.handle(command);

    // Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(lockPort).execute(runnableCaptor.capture(), eq(order.getCustomerId().toString() + ":" + order.getAssetName()));
    runnableCaptor.getValue().run();

    verify(updateAssetStrategyFactory, never()).updateAsset(command);
  }

  @Test
  void handle_shouldProcessCommand_whenInboxIsNull() {
    // Given
    var order = Order.builder().id(UUID.randomUUID()).customerId(UUID.randomUUID()).assetName("ING").build();
    var command = UpdateAssetCommand.builder().outboxId(UUID.randomUUID()).order(order).build();
    when(inboxPort.retrieveInboxById(command.getOutboxId())).thenReturn(null);

    // When
    handler.handle(command);

    // Then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(lockPort).execute(runnableCaptor.capture(), eq(order.getCustomerId() + ":" + order.getAssetName()));
    runnableCaptor.getValue().run();

    verify(updateAssetStrategyFactory).updateAsset(command);
  }
}
