package com.inghubs.asset.strategies;

import com.inghubs.asset.command.CheckValidationAndUpdateAssetCommand;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.asset.strategies.abstracts.AssetUpdateStrategy;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.order.model.Order;
import com.inghubs.outbox.port.OutboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Service("BUY" + AssetUpdateStrategy.suffix)
public class BuySideValidation implements AssetUpdateStrategy {

  public static final String TRY = "TRY";
  private final AssetPort assetPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;

  @Override
  public void checkValidationAndUpdateAsset(CheckValidationAndUpdateAssetCommand command) {
    Order order = command.getOrder();

    Asset tryAsset = assetPort.retrieveCustomerTRYAsset(order.getCustomerId());
    Asset buyAsset = assetPort.retrieveCustomerAsset(order.getAssetId(), order.getCustomerId());

    transactionTemplate.executeWithoutResult(status -> {
      boolean isValid = tryAsset != null
          && buyAsset != null
          && buyAsset.getAssetName().equals(order.getAssetName())
          && !buyAsset.getAssetName().equals(TRY)
          && tryAsset.getUsableSize().compareTo(order.getSize().multiply(order.getPrice())) >= 0;

      if(isValid) {
        tryAsset.reserveForBuyOrder(order);
        assetPort.updateOrSaveAsset(tryAsset);
        outboxPort.createOrderValidatedOutboxEntity(order);
      } else {
        outboxPort.createOrderRejectedOutboxEntity(order);
      }

      inboxPort.createOrderCreatedInboxEntity(command.getOutboxId(), order);
    });
  }
}
