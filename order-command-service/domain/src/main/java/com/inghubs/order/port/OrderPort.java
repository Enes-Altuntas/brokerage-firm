package com.inghubs.order.port;

import com.inghubs.order.model.Order;

public interface OrderPort {

  Order createOrder(Order order);

}
