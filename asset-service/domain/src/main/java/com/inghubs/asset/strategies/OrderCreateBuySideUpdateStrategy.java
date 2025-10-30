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
@Service("BUY" + OrderCreateAssetUpdateStrategy.suffix)
public class OrderCreateBuySideUpdateStrategy implements OrderCreateAssetUpdateStrategy {

  public static final String ORDER_VALIDATED = "ORDER_VALIDATED";
  public static final String ORDER_REJECTED = "ORDER_REJECTED";
  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void updateAsset(UpdateAssetCommand command) {
    Order order = command.getOrder();

    Asset tryAsset = assetPort.retrieveCustomerAsset("TRY", order.getCustomerId());
    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = tryAsset != null
          && tryAsset.getUsableSize().compareTo(order.getSize().multiply(order.getPrice())) >= 0;

      if(isValid) {
        tryAsset.reserveForBuyOrder(order);
        assetPort.updateOrSaveAsset(tryAsset);
        outboxPort.createOrderOutboxEntity(ORDER_VALIDATED, order);
      } else {
        outboxPort.createOrderOutboxEntity(ORDER_REJECTED, order);
      }

      inboxPort.createInboxEntity(command);
    });
  }
}
