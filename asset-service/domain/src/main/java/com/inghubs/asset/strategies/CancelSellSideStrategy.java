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
@Service("SELL" + CancelAssetStrategy.suffix)
public class CancelSellSideStrategy implements CancelAssetStrategy {

  public static final String ORDER_CANCEL_CONFIRMED = "ORDER_CANCEL_CONFIRMED";
  public static final String ORDER_CANCEL_REJECTED = "ORDER_CANCEL_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void cancelAsset(RollbackAssetCommand command) {
    log.info("Canceling asset for sell order: {}", command.getOrder().getId());
    Order order = command.getOrder();

    Asset asset = assetPort.retrieveCustomerAsset(order.getAssetName(), order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null
          && !asset.getAssetName().equals(TRY)
          && asset.getUsableSize().compareTo(order.getSize()) >= 0;

      String outboxEventType;
      if(isValid) {
        log.info("Asset is valid for cancellation. Rolling back asset for sell order: {}", order.getId());
        asset.rollbackForSellOrder(order);
        assetPort.createOrUpdateAsset(asset);
        outboxEventType = ORDER_CANCEL_CONFIRMED;
      } else {
        log.warn("Asset is not valid for cancellation. Rejecting cancellation for sell order: {}", order.getId());
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
    log.info("Finished canceling asset for sell order: {}", command.getOrder().getId());
  }
}
