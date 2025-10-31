package com.inghubs.asset.handler;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.factory.abstracts.CancelAssetStrategyFactory;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RollbackAssetCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<RollbackAssetCommand> {

  private final InboxPort inboxPort;
  private final LockPort lockPort;
  private final CancelAssetStrategyFactory cancelAssetStrategyFactory;

  public RollbackAssetCommandHandler(InboxPort inboxPort, LockPort lockPort,
      CancelAssetStrategyFactory cancelAssetStrategyFactory) {
    this.inboxPort = inboxPort;
    this.lockPort = lockPort;
    this.cancelAssetStrategyFactory = cancelAssetStrategyFactory;
    register(RollbackAssetCommand.class, this);
  }

  @Override
  public void handle(RollbackAssetCommand command) {
    log.info("Handling RollbackAssetCommand for order: {}", command.getOrder().getId());

    lockPort.execute(() -> {

      Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
      if (currentInbox != null) {
        log.info("Inbox already exists for outboxId: {}. Skipping command handling.", command.getOutboxId());
        return;
      }

      cancelAssetStrategyFactory.cancelAsset(command);

    }, command.getOrder().getCustomerId().toString() + ":" + command.getOrder().getAssetName());

    log.info("Finished handling RollbackAssetCommand for order: {}", command.getOrder().getId());
  }
}
