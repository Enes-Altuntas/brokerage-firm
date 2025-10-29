package com.inghubs.adapters.order.jpa;

import com.inghubs.adapters.order.jpa.entity.OrderEntity;
import com.inghubs.adapters.order.jpa.repository.OrderRepository;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderPort {

  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public Order createOrder(Order order) {

    OrderEntity entity = new OrderEntity(order);

    OrderEntity savedOrderEntity = orderRepository.save(entity);

    return savedOrderEntity.toDomain();
  }
}
