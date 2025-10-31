package com.inghubs.asset.strategies;

import com.inghubs.asset.command.RollbackAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.CancelAssetStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@RequiredArgsConstructor
@Service("BUY" + CancelAssetStrategy.suffix)
public class CancelBuySideStrategy implements CancelAssetStrategy {

  public static final String TRY = "TRY";
  public static final String ORDER_CANCEL_CONFIRMED = "ORDER_CANCEL_CONFIRMED";
  public static final String ORDER_CANCEL_REJECTED = "ORDER_CANCEL_REJECTED";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void cancelAsset(RollbackAssetCommand command) {
    log.info("Canceling asset for buy order: {}", command.getOrder().getId());
    Order order = command.getOrder();

    Asset tryAsset = assetPort.retrieveCustomerAsset("TRY", order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = tryAsset != null
          && tryAsset.getUsableSize().compareTo(order.getSize().multiply(order.getPrice())) >= 0;

      String outboxEventType;
      if(isValid) {
        log.info("Asset is valid for cancellation. Rolling back asset for buy order: {}", order.getId());
        tryAsset.rollbackForBuyOrder(order);
        assetPort.createOrUpdateAsset(tryAsset);
        outboxEventType = ORDER_CANCEL_CONFIRMED;
      } else {
        log.warn("Asset is not valid for cancellation. Rejecting cancellation for buy order: {}", order.getId());
        outboxEventType = ORDER_CANCEL_REJECTED;
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
    log.info("Finished canceling asset for buy order: {}", command.getOrder().getId());
  }
}
