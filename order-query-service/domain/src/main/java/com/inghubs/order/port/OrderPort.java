package com.inghubs.order.port;

import com.inghubs.order.model.Order;
import java.util.UUID;

public interface OrderPort {

  void createOrUpdateOrder(Order order);

  Order retrieveOrder(UUID id);

}
