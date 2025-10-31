package com.inghubs.adapters.order.rest;

import com.inghubs.adapters.order.rest.model.request.AdminCancelOrderRequest;
import com.inghubs.adapters.order.rest.model.request.AdminCreateOrderRequest;
import com.inghubs.adapters.order.rest.model.request.AdminMatchOrderRequest;
import com.inghubs.adapters.order.rest.model.response.CreateOrderResponse;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.common.rest.model.GenericResponse;
import com.inghubs.order.command.CancelRequestOrderCommand;
import com.inghubs.order.command.CreateOrderCommand;
import com.inghubs.order.command.MatchRequestOrderCommand;
import com.inghubs.order.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/order")
public class AdminOrderController extends BaseController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<GenericResponse<CreateOrderResponse>> adminCreateOrder(
      @Valid @RequestBody AdminCreateOrderRequest request) {

    CreateOrderCommand command = request.toCommand();

    Order dto = publish(Order.class, command);

    CreateOrderResponse response = CreateOrderResponse.toResponse(dto);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(respond(response));
  }

  @PutMapping("/cancel")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<Void>> adminRequestCancelOrder(@Valid @RequestBody
      AdminCancelOrderRequest request) {

    CancelRequestOrderCommand command = request.toCommand();

    publish(command);

    return ResponseEntity.status(HttpStatus.OK).body(respond());
  }

  @PostMapping("/match")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenericResponse<Void>> adminRequestMatchOrder(
      @Valid @RequestBody AdminMatchOrderRequest request) {

    MatchRequestOrderCommand command = request.toCommand();

    publish(command);

    return ResponseEntity.status(HttpStatus.OK).body(respond());
  }
}
