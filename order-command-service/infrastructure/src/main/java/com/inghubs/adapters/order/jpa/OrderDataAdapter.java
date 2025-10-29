package com.inghubs.adapters.order.jpa;

import com.inghubs.adapters.order.jpa.entity.OrderEntity;
import com.inghubs.adapters.order.jpa.repository.OrderRepository;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderPort {

  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public Order createOrUpdateOrder(Order order) {

    OrderEntity entity = new OrderEntity(order);

    OrderEntity savedOrderEntity = orderRepository.save(entity);

    return savedOrderEntity.toDomain();
  }

  @Override
  public Order retrieveOrder(UUID orderId) {
    Optional<OrderEntity> entity = orderRepository.findById(orderId);

    if (entity.isEmpty()) {
      return null;
    }

    return entity.map(OrderEntity::toDomain).orElse(null);
  }
}
