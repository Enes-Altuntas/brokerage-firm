package com.inghubs.outbox.port;

import com.inghubs.order.model.Order;
import com.inghubs.outbox.model.Outbox;

public interface OutboxPort {

  void createOrderRejectedOutboxEntity(Order order);

  void createOrderValidatedOutboxEntity(Order order);
}
