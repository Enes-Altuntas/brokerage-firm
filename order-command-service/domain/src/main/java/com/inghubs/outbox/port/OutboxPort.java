package com.inghubs.outbox.port;

import com.inghubs.order.model.Order;

public interface OutboxPort {

  void createOrderCreatedOutboxEntity(Order order);

  void createOrderUpdatedOutboxEntity(Order order);

}
