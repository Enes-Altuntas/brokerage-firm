package com.inghubs.asset.strategies;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.OrderCancelAssetUpdateStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service("SELL" + OrderCancelAssetUpdateStrategy.suffix)
public class OrderCancelSellSideUpdateStrategy implements OrderCancelAssetUpdateStrategy {

  public static final String ORDER_CANCEL_CONFIRMED = "ORDER_CANCEL_CONFIRMED";
  public static final String ORDER_CANCEL_REJECTED = "ORDER_CANCEL_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    Order order = command.getOrder();

    Asset asset = assetPort.retrieveCustomerAsset(order.getAssetName(), order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null
          && !asset.getAssetName().equals(TRY)
          && asset.getUsableSize().compareTo(order.getSize()) >= 0;

      if(isValid) {
        asset.rollbackForSellOrder(order);
        assetPort.updateOrSaveAsset(asset);
        outboxPort.createOrderOutboxEntity(ORDER_CANCEL_CONFIRMED, order);
      } else {
        outboxPort.createOrderOutboxEntity(ORDER_CANCEL_REJECTED, order);
      }

      inboxPort.createInboxEntity(command);
    });
  }
}
