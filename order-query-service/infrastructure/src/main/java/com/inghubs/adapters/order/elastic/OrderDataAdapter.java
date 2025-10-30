package com.inghubs.adapters.order.elastic;

import com.inghubs.adapters.order.elastic.criteria.OrderCriteriaBuilder;
import com.inghubs.adapters.order.elastic.document.OrderDocument;
import com.inghubs.adapters.order.elastic.repository.OrderRepository;
import com.inghubs.adapters.order.elastic.service.OrderQueryService;
import com.inghubs.adapters.order.rest.model.OrderFilterRequest;
import com.inghubs.common.rest.model.PaginationRequest;
import com.inghubs.order.model.Order;
import com.inghubs.order.port.OrderPort;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDataAdapter implements OrderPort, OrderQueryService {

  private final ElasticsearchOperations elasticsearchOperations;
  private final OrderRepository orderRepository;

  @Override
  public void createOrUpdateOrder(Order order) {

    OrderDocument orderDocument = new OrderDocument(order);

    orderRepository.save(orderDocument);
  }

  @Override
  public Order retrieveOrder(UUID id) {

    Optional<OrderDocument> entity = orderRepository.findById(id);

    if(entity.isEmpty()) {
      return null;
    }

    return entity.map(OrderDocument::toDomain).orElse(null);
  }

  @Override
  public Page<OrderDocument> query(OrderFilterRequest filterRequest, PaginationRequest paginationRequest) {

    Criteria criteria = OrderCriteriaBuilder.buildCriteria(filterRequest);

    Pageable pageable = buildPageable(paginationRequest);

    CriteriaQuery query = new CriteriaQuery(criteria);
    query.setPageable(pageable);

    SearchHits<OrderDocument> searchHits = elasticsearchOperations.search(query, OrderDocument.class);

    Page<SearchHit<OrderDocument>> searchHitPage = SearchHitSupport.searchPageFor(searchHits, pageable);

    return searchHitPage.map(SearchHit::getContent);
  }

  @Override
  public OrderDocument query(OrderFilterRequest filterRequest) {
    Optional<OrderDocument> orderDocument = orderRepository
        .findByIdAndCustomerId(filterRequest.getOrderId(), filterRequest.getCustomerId());

    return orderDocument.orElse(null);
  }

  private Pageable buildPageable(PaginationRequest paginationRequest) {
    Sort.Direction direction = "ASC".equalsIgnoreCase(paginationRequest.direction())
        ? Sort.Direction.ASC : Direction.DESC;
    Sort sort = Sort.by(direction, paginationRequest.sortBy() == null ? "createdAt" : paginationRequest.sortBy());

    return PageRequest.of(paginationRequest.page(), paginationRequest.size(), sort);
  }
}
