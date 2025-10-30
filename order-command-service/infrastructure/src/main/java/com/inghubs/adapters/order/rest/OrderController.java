package com.inghubs.adapters.order.rest;

import com.inghubs.adapters.order.rest.model.request.CreateOrderRequest;
import com.inghubs.adapters.order.rest.model.response.CreateOrderResponse;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.common.rest.model.GenericResponse;
import com.inghubs.order.command.CancelRequestOrderCommand;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.model.Order;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController extends BaseController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<GenericResponse<CreateOrderResponse>> createOrder(
      @RequestHeader(name = "x-customer-id")  UUID customerId,
      @Valid @RequestBody CreateOrderRequest request) {

    CreateOrderCommand command = request.toCommand(customerId);

    Order dto = publish(Order.class, command);

    CreateOrderResponse response = CreateOrderResponse.toResponse(dto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(respond(response));
  }

  @PutMapping("/cancel/{orderId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<Void>> requestCancelOrder(
      @RequestHeader(name = "x-customer-id")  UUID customerId,
      @PathVariable UUID orderId) {

    CancelRequestOrderCommand command = CancelRequestOrderCommand.builder()
        .orderId(orderId)
        .customerId(customerId)
        .build();

    publish(command);

    return ResponseEntity.status(HttpStatus.OK).body(respond());
  }
}
