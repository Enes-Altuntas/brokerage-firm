package com.inghubs.asset.handler;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateAssetCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<UpdateAssetCommand> {

  private final InboxPort inboxPort;
  private final AssetUpdateStrategyFactory assetUpdateStrategyFactory;

  public UpdateAssetCommandHandler(InboxPort inboxPort,
      AssetUpdateStrategyFactory assetUpdateStrategyFactory) {
    this.inboxPort = inboxPort;
    this.assetUpdateStrategyFactory = assetUpdateStrategyFactory;
    register(UpdateAssetCommand.class, this);
  }

  @Override
  public void handle(UpdateAssetCommand command) {
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (currentInbox != null) {
      return;
    }

    assetUpdateStrategyFactory.updateAsset(command);
  }
}
