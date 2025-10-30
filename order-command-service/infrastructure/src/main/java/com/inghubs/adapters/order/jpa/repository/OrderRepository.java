package com.inghubs.adapters.order.jpa.repository;

import com.inghubs.adapters.order.jpa.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

  Optional<OrderEntity> findByIdAndCustomerId(UUID orderId, UUID customerId);
}
