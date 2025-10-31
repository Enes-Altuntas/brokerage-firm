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
@Service("SELL" + UpdateAssetStrategy.suffix)
public class UpdateSellSideStrategy implements UpdateAssetStrategy {

  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  public static final String ORDER_REJECTED = "ORDER_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final InboxPort inboxPort;
  private final OutboxPort outboxPort;
  private final TransactionTemplate transactionTemplate;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    log.info("Updating asset for sell order: {}", command.getOrder().getId());
    Order order = command.getOrder();

    Asset asset = assetPort.retrieveCustomerAsset(order.getAssetName(), order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null
          && !asset.getAssetName().equals(TRY)
          && asset.getUsableSize().compareTo(order.getSize()) >= 0;

      String outboxEventType;
      if(isValid) {
        log.info("Asset is valid. Reserving asset for sell order: {}", order.getId());
        asset.reserveForSellOrder(order);
        assetPort.createOrUpdateAsset(asset);
        outboxEventType = ORDER_VALIDATED;
      } else {
        log.warn("Asset is not valid. Rejecting sell order: {}", order.getId());
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
    log.info("Finished updating asset for sell order: {}", command.getOrder().getId());
  }
}
