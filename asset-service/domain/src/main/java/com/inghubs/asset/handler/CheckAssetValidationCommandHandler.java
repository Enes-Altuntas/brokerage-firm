package com.inghubs.asset.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.asset.command.CheckAssetValidationCommand;
import com.inghubs.asset.factory.abstracts.AssetUpdateStrategyFactory;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import org.springframework.stereotype.Service;

@Service
public class CheckAssetValidationCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CheckAssetValidationCommand> {

  private final InboxPort inboxPort;
  private final ObjectMapper objectMapper;
  private final AssetUpdateStrategyFactory assetUpdateStrategyFactory;
  private final AssetPort assetPort;

  public CheckAssetValidationCommandHandler(InboxPort inboxPort, ObjectMapper objectMapper,
      AssetUpdateStrategyFactory assetUpdateStrategyFactory, AssetPort assetPort) {
    this.inboxPort = inboxPort;
    this.objectMapper = objectMapper;
    this.assetUpdateStrategyFactory = assetUpdateStrategyFactory;
    this.assetPort = assetPort;
    register(CheckAssetValidationCommand.class, this);
  }

  @Override
  public void handle(CheckAssetValidationCommand command) {
    Inbox currentInbox = inboxPort.retrieveInboxById(command.getOutboxId());
    if (currentInbox != null) {
      return;
    }

    assetUpdateStrategyFactory.checkValidationAndUpdateAsset(command);
  }
}
