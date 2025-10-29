package com.inghubs.asset.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.AssetUpdateStrategy;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.model.Outbox;
import com.inghubs.outbox.port.OutboxPort;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service("SELL" + AssetUpdateStrategy.suffix)
public class SellSideValidation implements AssetUpdateStrategy {

  private final AssetPort assetPort;
  private final InboxPort inboxPort;
  private final OutboxPort outboxPort;
  private final TransactionTemplate transactionTemplate;

  @Override
  public void checkValidationAndUpdateAsset(UUID outboxId, Order order) {
    Asset asset = assetPort.retrieveCustomerAsset(order.getAssetId(), order.getCustomerId());

    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null &&
          asset.getUsableSize().compareTo(order.getSize()) >= 0;

      if(isValid) {
        asset.reserveForSellOrder(order);
        assetPort.updateOrSaveAsset(asset);
        outboxPort.createOrderValidatedOutboxEntity(order);
      } else {
        outboxPort.createOrderRejectedOutboxEntity(order);
      }

      inboxPort.createOrderCreatedInboxEntity(outboxId, order);
    });
  }
}
