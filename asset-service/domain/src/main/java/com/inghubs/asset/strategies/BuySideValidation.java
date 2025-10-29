package com.inghubs.asset.strategies;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Service("BUY" + AssetUpdateStrategy.suffix)
public class BuySideValidation implements AssetUpdateStrategy {

  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void checkValidationAndUpdateAsset(UUID outboxId, Order order) {
    Asset asset = assetPort.retrieveCustomerTRYAsset(order.getCustomerId());

    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null &&
          asset.getUsableSize().compareTo(order.getSize().multiply(order.getPrice())) >= 0;

      if(isValid) {
        asset.reserveForBuyOrder(order);
        assetPort.updateOrSaveAsset(asset);
        outboxPort.createOrderValidatedOutboxEntity(order);
      } else {
        outboxPort.createOrderRejectedOutboxEntity(order);
      }

      inboxPort.createOrderCreatedInboxEntity(outboxId, order);
    });
  }
}
