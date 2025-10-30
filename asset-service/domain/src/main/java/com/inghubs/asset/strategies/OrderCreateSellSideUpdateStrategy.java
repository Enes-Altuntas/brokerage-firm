package com.inghubs.asset.strategies;

import com.inghubs.asset.command.UpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.OrderCreateAssetUpdateStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service("SELL" + OrderCreateAssetUpdateStrategy.suffix)
public class OrderCreateSellSideUpdateStrategy implements OrderCreateAssetUpdateStrategy {

  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  public static final String ORDER_REJECTED = "ORDER_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final InboxPort inboxPort;
  private final OutboxPort outboxPort;
  private final TransactionTemplate transactionTemplate;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    Order order = command.getOrder();

    Asset asset = assetPort.retrieveCustomerAsset(order.getAssetId(), order.getCustomerId());

    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = asset != null
          && asset.getAssetName().equals(order.getAssetName())
          && !asset.getAssetName().equals(TRY)
          && asset.getUsableSize().compareTo(order.getSize()) >= 0;

      if(isValid) {
        asset.reserveForSellOrder(order);
        assetPort.updateOrSaveAsset(asset);
        outboxPort.createOrderOutboxEntity(ORDER_VALIDATED, order);
      } else {
        outboxPort.createOrderOutboxEntity(ORDER_REJECTED, order);
      }

      inboxPort.createInboxEntity(command);
    });
  }
}
