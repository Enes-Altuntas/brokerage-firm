package com.inghubs.adapters.order.elastic.service;

import com.inghubs.adapters.order.elastic.document.OrderDocument;
import com.inghubs.adapters.order.rest.model.OrderFilterRequest;
import com.inghubs.common.rest.model.PaginationRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderQueryService {

  Page<OrderDocument> query(OrderFilterRequest filterRequest, PaginationRequest paginationRequest);

  OrderDocument query(UUID orderId, UUID customerId);

}
