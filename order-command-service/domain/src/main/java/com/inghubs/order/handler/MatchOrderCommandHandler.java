package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.inbox.model.Inbox;
import com.inghubs.inbox.port.InboxPort;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.MatchOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class MatchOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<MatchOrderCommand> {

  private static final String ORDER_MATCH_CONFIRMED = "ORDER_MATCH_CONFIRMED";
  private static final String ORDER_UPDATED = "ORDER_UPDATED";
  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final InboxPort inboxPort;
  private final LockPort lockPort;
  private final TransactionTemplate transactionTemplate;

  public MatchOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort, InboxPort inboxPort,
      LockPort lockPort,
      TransactionTemplate transactionTemplate) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.inboxPort = inboxPort;
    this.lockPort = lockPort;
    this.transactionTemplate = transactionTemplate;
    register(MatchOrderCommand.class, this);
  }

  @Override
  public void handle(MatchOrderCommand command) {
    log.info("Handling match order command: {}", command);
    String buyOrderLock = command.getMatchOrder().getBuyOrderId().toString();
    String sellOrderLock = command.getMatchOrder().getSellOrderId().toString();

    lockPort.execute(() -> {

      Inbox inbox = inboxPort.retrieveInboxById(command.getOutboxId());
      if (inbox != null) {
        log.info("Inbox already contains outboxId: {}, skipping match order command", command.getOutboxId());
        return;
      }

      Order buyOrder = orderPort.retrieveOrder(command.getMatchOrder().getBuyOrderId(),
          command.getMatchOrder().getBuyerCustomerId());
      if (buyOrder == null) {
        log.error("Buy order not found for orderId: {}", command.getMatchOrder().getBuyOrderId());
        throw new OrderBusinessException("2000");
      }

      if (buyOrder.getStatus() != OrderStatus.PENDING
          && buyOrder.getStatus() != OrderStatus.PARTIALLY_MATCHED) {
        log.error("Buy order status is not PENDING or PARTIALLY_MATCHED for orderId: {}",
            buyOrder.getId());
        throw new OrderBusinessException("2001");
      }

      Order sellOrder = orderPort.retrieveOrder(command.getMatchOrder().getSellOrderId(),
          command.getMatchOrder().getSellerCustomerId());
      if (sellOrder == null) {
        log.error("Sell order not found for orderId: {}", command.getMatchOrder().getSellOrderId());
        throw new OrderBusinessException("2000");
      }

      if (sellOrder.getStatus() != OrderStatus.PENDING
          && sellOrder.getStatus() != OrderStatus.PARTIALLY_MATCHED) {
        log.error("Sell order status is not PENDING or PARTIALLY_MATCHED for orderId: {}",
            sellOrder.getId());
        throw new OrderBusinessException("2001");
      }

      if (command.getEventType().equals(ORDER_MATCH_CONFIRMED)) {
        buyOrder.match(command.getMatchOrder());
        sellOrder.match(command.getMatchOrder());
        log.info("Matched buy order {} with sell order {}", buyOrder.getId(), sellOrder.getId());
      }

      transactionTemplate.executeWithoutResult(status -> {
        orderPort.createOrUpdateOrder(buyOrder);
        orderPort.createOrUpdateOrder(sellOrder);
        inboxPort.createInboxEntity(command.getOutboxId(), command.getEventType(),
            command.getMatchOrder().getBuyOrderId(), command.getMatchOrder());
        outboxPort.createOrderOutboxEntity(ORDER_UPDATED, buyOrder.getId(), buyOrder);
        outboxPort.createOrderOutboxEntity(ORDER_UPDATED, sellOrder.getId(), sellOrder);
        log.info("Match order command processed successfully for buy order {} and sell order {}",
            buyOrder.getId(), sellOrder.getId());
      });

    }, buyOrderLock, sellOrderLock);
  }
}
