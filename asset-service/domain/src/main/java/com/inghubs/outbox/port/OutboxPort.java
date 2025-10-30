package com.inghubs.outbox.port;

import com.inghubs.order.model.Order;

public interface OutboxPort {

  void createOrderOutboxEntity(String eventType, Order order);

}
