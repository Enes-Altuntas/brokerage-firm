package com.inghubs.adapters.order.elastic.repository;

import com.inghubs.adapters.order.elastic.document.OrderDocument;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrderRepository extends ElasticsearchRepository<OrderDocument, UUID> {

  public Optional<OrderDocument> findByIdAndCustomerId(UUID id, UUID customerId);
}
