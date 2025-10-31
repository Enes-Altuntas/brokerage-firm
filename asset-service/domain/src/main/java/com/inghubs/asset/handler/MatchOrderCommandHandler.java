package com.inghubs.asset.handler;

import com.inghubs.asset.command.MatchOrderCommand;
import com.inghubs.asset.exception.AssetBusinessException;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.port.AssetPort;
import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.LockPort;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class MatchOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<MatchOrderCommand> {

  public static final String TRY = "TRY";
  private final InboxPort inboxPort;
  private final AssetPort assetPort;
  private final LockPort  lockPort;
  private final TransactionTemplate transactionTemplate;
  private final OutboxPort outboxPort;

  public MatchOrderCommandHandler(InboxPort inboxPort, AssetPort assetPort, LockPort lockPort,
      TransactionTemplate transactionTemplate, OutboxPort outboxPort) {
    this.inboxPort = inboxPort;
    this.assetPort = assetPort;
    this.lockPort = lockPort;
    this.transactionTemplate = transactionTemplate;
    register(MatchOrderCommand.class, this);
    this.outboxPort = outboxPort;
  }

  @Override
  public void handle(MatchOrderCommand command) {
    log.info("Handling MatchOrderCommand: {}", command);

    String buyAssetLock = command.getMatchOrder().getBuyerCustomerId() + ":" + command.getMatchOrder().getAssetName();
    String sellAssetLock = command.getMatchOrder().getSellerCustomerId() + ":" + command.getMatchOrder().getAssetName();

    lockPort.execute(() -> processCommand(command), buyAssetLock, sellAssetLock);

    log.info("Finished handling MatchOrderCommand for buyOrder={} sellOrder={}",
        command.getMatchOrder().getBuyOrderId(), command.getMatchOrder().getSellOrderId());
  }

  private void processCommand(MatchOrderCommand command) {

    if (inboxPort.retrieveInboxById(command.getOutboxId()) != null) {
      log.info("Inbox already exists for outboxId {} — skipping", command.getOutboxId());
      return;
    }

    Asset buyerTRY = findAssetOrThrow(TRY, command.getMatchOrder().getBuyerCustomerId());
    Asset buyerAsset = findOrCreateAsset(command.getMatchOrder().getAssetName(), command.getMatchOrder().getBuyerCustomerId());
    Asset sellerAsset = findAssetOrThrow(command.getMatchOrder().getAssetName(), command.getMatchOrder().getSellerCustomerId());
    Asset sellerTRY = findAssetOrThrow(TRY, command.getMatchOrder().getSellerCustomerId());

    BigDecimal totalCost = command.getMatchOrder().getMatchPrice().multiply(command.getMatchOrder().getMatchSize());
    validateBalances(buyerTRY, sellerAsset, totalCost, command.getMatchOrder().getMatchSize());

    applyMatchAdjustments(command, buyerTRY, buyerAsset, sellerTRY, sellerAsset, totalCost, command.getMatchOrder().getMatchSize());
  }

  private Asset findAssetOrThrow(String assetName, UUID customerId) {
    Asset asset = assetPort.retrieveCustomerAsset(assetName, customerId);
    if (asset == null)
      throw new AssetBusinessException("2000");
    return asset;
  }

  private Asset findOrCreateAsset(String assetName, UUID customerId) {
    Asset asset = assetPort.retrieveCustomerAsset(assetName, customerId);
    return asset != null ? asset : Asset.builder()
        .customerId(customerId)
        .assetName(assetName)
        .size(BigDecimal.ZERO)
        .usableSize(BigDecimal.ZERO)
        .build();
  }

  private void validateBalances(Asset buyerTRY, Asset sellerAsset, BigDecimal totalCost, BigDecimal matchSize) {
    if (buyerTRY.getUsableSize().compareTo(totalCost) < 0)
      throw new AssetBusinessException("2001");
    if (sellerAsset.getUsableSize().compareTo(matchSize) < 0)
      throw new AssetBusinessException("2001");
  }

  private void applyMatchAdjustments(
      MatchOrderCommand command,
      Asset buyerTRY, Asset buyerAsset,
      Asset sellerTRY, Asset sellerAsset,
      BigDecimal totalCost, BigDecimal matchSize
  ) {
    buyerTRY.setSize(buyerTRY.getSize().subtract(totalCost).max(BigDecimal.ZERO));
    if(command.getMatchOrder().getPriceDifference().compareTo(BigDecimal.ZERO) > 0) {
      buyerTRY.setUsableSize(buyerTRY.getUsableSize().add(command.getMatchOrder().getPriceDifference().multiply(matchSize)));
    }
    buyerAsset.setUsableSize(buyerAsset.getUsableSize().add(matchSize).max(BigDecimal.ZERO));
    buyerAsset.setSize(buyerAsset.getSize().add(matchSize).max(BigDecimal.ZERO));

    sellerTRY.setUsableSize(sellerTRY.getUsableSize().add(totalCost).max(BigDecimal.ZERO));
    sellerTRY.setSize(sellerTRY.getSize().add(totalCost).max(BigDecimal.ZERO));
    sellerAsset.setSize(sellerAsset.getSize().subtract(matchSize).max(BigDecimal.ZERO));

    transactionTemplate.executeWithoutResult(status -> {
      assetPort.createOrUpdateAsset(buyerTRY);
      assetPort.createOrUpdateAsset(buyerAsset);
      assetPort.createOrUpdateAsset(sellerTRY);
      assetPort.createOrUpdateAsset(sellerAsset);

      inboxPort.createInboxEntity(command.getOutboxId(), "ORDER_MATCH_REQUESTED", command.getAggregateId(), command.getMatchOrder());
      outboxPort.createOrderOutboxEntity("ORDER_MATCH_CONFIRMED", command.getAggregateId(), command.getMatchOrder());
    });
  }
}
