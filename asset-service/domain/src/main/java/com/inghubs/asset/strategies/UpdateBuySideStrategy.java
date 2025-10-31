package com.inghubs.asset.strategies;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.UpdateAssetStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@RequiredArgsConstructor
@Service("BUY" + UpdateAssetStrategy.suffix)
public class UpdateBuySideStrategy implements UpdateAssetStrategy {

  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  public static final String ORDER_REJECTED = "ORDER_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    log.info("Updating asset for buy order: {}", command.getOrder().getId());
    Order order = command.getOrder();

    Asset tryAsset = assetPort.retrieveCustomerAsset("TRY", order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = tryAsset != null
          && tryAsset.getUsableSize().compareTo(order.getSize().multiply(order.getPrice())) >= 0;

      String outboxEventType;
      if(isValid) {
        log.info("Asset is valid. Reserving asset for buy order: {}", order.getId());
        tryAsset.reserveForBuyOrder(order);
        assetPort.createOrUpdateAsset(tryAsset);
        outboxEventType = ORDER_VALIDATED;
      } else {
        log.warn("Asset is not valid. Rejecting buy order: {}", order.getId());
        outboxEventType = ORDER_REJECTED;
      }

      outboxPort.createOrderOutboxEntity(
          outboxEventType,
          order.getId(),
          order);

      inboxPort.createInboxEntity(
          command.getOutboxId(),
          command.getEventType(),
          command.getOrder().getId(),
          order);
    });
    log.info("Finished updating asset for buy order: {}", command.getOrder().getId());
  }
}
