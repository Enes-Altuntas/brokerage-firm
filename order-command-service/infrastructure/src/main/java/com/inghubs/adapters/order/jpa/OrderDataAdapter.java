package com.inghubs.adapters.order.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.adapters.order.jpa.entity.OrderEntity;
import com.inghubs.adapters.order.jpa.repository.OrderRepository;
import com.inghubs.adapters.outbox.jpa.entity.OutboxEntity;
import com.inghubs.adapters.outbox.jpa.service.OutboxDataService;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderPort {

  private final OrderRepository orderRepository;
  private final OutboxDataService outboxDataService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @Transactional
  public Order createOrder(Order order) {

    OrderEntity entity = new OrderEntity(order);

    OrderEntity savedOrderEntity = orderRepository.save(entity);

    OutboxEntity outboxEntity = prepareOutboxEntityForOrderCreatedEvent(savedOrderEntity);

    outboxDataService.save(outboxEntity);

    return savedOrderEntity.toDomain();
  }

  private OutboxEntity prepareOutboxEntityForOrderCreatedEvent(OrderEntity savedEntity) {
    JsonNode payload = objectMapper.valueToTree(savedEntity);

    return OutboxEntity.builder()
        .payload(payload)
        .aggregateId(savedEntity.getId())
        .aggregateType("ORDER")
        .eventType("ORDER_CREATED")
        .createdBy(savedEntity.getCreatedBy())
        .updatedBy(savedEntity.getUpdatedBy())
        .build();
  }
}
