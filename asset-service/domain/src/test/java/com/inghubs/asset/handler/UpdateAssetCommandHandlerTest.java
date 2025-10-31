package com.inghubs.asset.handler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import com.inghubs.order.model.Order;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateAssetCommandHandlerTest {

  @InjectMocks
  private UpdateAssetCommandHandler commandHandler;

  @Mock
  private InboxPort inboxPort;

  @Mock
  private LockPort lockPort;

  @Mock
  private AssetUpdateStrategyFactory assetUpdateStrategyFactory;

  @Test
  void handle_shouldProcessCommand_whenInboxIsNull() {
    // Given
    UUID outboxId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    String assetName = "BTC";
    Order order = Order.builder().customerId(customerId).assetName(assetName).build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .outboxId(outboxId)
        .order(order)
        .build();

    when(inboxPort.retrieveInboxById(outboxId)).thenReturn(null);

    // When
    commandHandler.handle(command);

    // Then
    verify(lockPort).lock(customerId, assetName);
    verify(assetUpdateStrategyFactory).updateAsset(command);
    verify(lockPort).unlock(customerId, assetName);
  }

  @Test
  void handle_shouldNotProcessCommand_whenInboxIsNotNull() {
    // Given
    UUID outboxId = UUID.randomUUID();
    Order order = Order.builder().build();
    UpdateAssetCommand command = UpdateAssetCommand.builder()
        .outboxId(outboxId)
        .order(order)
        .build();

    when(inboxPort.retrieveInboxById(outboxId)).thenReturn(Inbox.builder().build());

    // When
    commandHandler.handle(command);

    // Then
    verify(lockPort).lock(order.getCustomerId(), order.getAssetName());
    verify(assetUpdateStrategyFactory, never()).updateAsset(command);
    verify(lockPort, never()).unlock(order.getCustomerId(), order.getAssetName());
  }
}
