package com.inghubs.adapters.order.elastic;

import com.inghubs.adapters.order.elastic.document.OrderDocument;
import com.inghubs.adapters.order.elastic.repository.OrderRepository;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderPort {

  private final OrderRepository orderRepository;

  @Override
  public void createOrder(Order order) {

    OrderDocument orderDocument = new OrderDocument(order);

    orderRepository.save(orderDocument);
  }
}
