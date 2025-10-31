package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.lock.port.LockPort;
import com.inghubs.order.command.MatchRequestOrderCommand;
import com.inghubs.order.exception.OrderBusinessException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import com.inghubs.order.port.OrderPort;
import com.inghubs.outbox.port.OutboxPort;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MatchRequestOrderCommandHandler  extends ObservableCommandPublisher
    implements VoidCommandHandler<MatchRequestOrderCommand> {

  private final OrderPort orderPort;
  private final OutboxPort outboxPort;
  private final LockPort lockPort;

  public MatchRequestOrderCommandHandler(OrderPort orderPort, OutboxPort outboxPort,
      LockPort lockPort) {
    this.orderPort = orderPort;
    this.outboxPort = outboxPort;
    this.lockPort = lockPort;
    register(MatchRequestOrderCommand.class, this);
  }

  @Override
  public void handle(MatchRequestOrderCommand command) {

    lockPort.execute(() -> {

      Order buyOrder = orderPort.retrieveOrder(command.getBuyOrderId());
      if (buyOrder == null) {
        throw new OrderBusinessException("2000");
      }
      if((buyOrder.getStatus() != OrderStatus.PENDING && buyOrder.getStatus() != OrderStatus.PARTIALLY_MATCHED )
          || buyOrder.getSide() != OrderSide.BUY) {
        throw new OrderBusinessException("2001");
      }

      Order sellOrder = orderPort.retrieveOrder(command.getSellOrderId());
      if (sellOrder == null) {
        throw new OrderBusinessException("2000");
      }
      if((sellOrder.getStatus() != OrderStatus.PENDING && sellOrder.getStatus() != OrderStatus.PARTIALLY_MATCHED )
          || sellOrder.getSide() != OrderSide.SELL) {
        throw new OrderBusinessException("2001");
      }

      if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) < 0) {
        throw new OrderBusinessException("2001");
      }

      BigDecimal matchPrice = sellOrder.getPrice();
      BigDecimal matchSize = buyOrder.getSize().min(sellOrder.getSize());
      BigDecimal priceDifference = buyOrder.getPrice().subtract(sellOrder.getPrice());

      MatchRequestOrderCommand event = MatchRequestOrderCommand.builder()
          .buyOrderId(buyOrder.getId())
          .sellOrderId(sellOrder.getId())
          .matchPrice(matchPrice)
          .matchSize(matchSize)
          .buyerCustomerId(buyOrder.getCustomerId())
          .sellerCustomerId(sellOrder.getCustomerId())
          .assetName(buyOrder.getAssetName())
          .priceDifference(priceDifference)
          .build();

      outboxPort.createOrderOutboxEntity("ORDER_MATCH_REQUESTED", UUID.randomUUID(), event);

    }, command.getBuyOrderId().toString(),command.getSellOrderId().toString());
  }
}
