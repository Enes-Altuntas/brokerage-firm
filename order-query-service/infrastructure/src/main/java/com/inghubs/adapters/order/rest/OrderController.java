package com.inghubs.adapters.order.rest;

import com.inghubs.adapters.order.elastic.document.OrderDocument;
import com.inghubs.adapters.order.elastic.service.OrderQueryService;
import com.inghubs.adapters.order.rest.model.OrderFilterRequest;
import com.inghubs.adapters.order.rest.model.OrderResponse;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.common.rest.model.GenericResponse;
import com.inghubs.common.rest.model.PaginationRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController extends BaseController {

  private final OrderQueryService orderQueryService;

  @GetMapping("/{orderId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<OrderResponse>> fetchOrders(
      @RequestHeader(name = "x-customer-id") UUID customerId,
      @PathVariable UUID orderId
      ) {

    OrderDocument orderDocument = orderQueryService.query(orderId, customerId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(respond(OrderResponse.toResponse(orderDocument)));
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<Page<OrderResponse>>> fetchOrder(
      @RequestHeader(name = "x-customer-id") UUID customerId,
      @ModelAttribute @Valid OrderFilterRequest filterRequest,
      @ModelAttribute @Valid PaginationRequest paginationRequest
  ) {

    Page<OrderDocument> query = orderQueryService.query(filterRequest, paginationRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(respond(query.map(OrderResponse::toResponse)));
  }
}
