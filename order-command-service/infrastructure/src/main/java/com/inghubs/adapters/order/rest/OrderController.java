package com.inghubs.adapters.order.rest;

import com.inghubs.adapters.order.rest.model.request.CreateOrderRequest;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.order.command.CancelOrderCommand;
import com.inghubs.order.command.CreateOrderCommand;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController extends BaseController {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest request) {

    CreateOrderCommand command = request.toCommand();

    publish(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }

  @PutMapping("/cancel/{orderId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> cancelOrder(@PathVariable UUID orderId) {

    CancelOrderCommand command = CancelOrderCommand.builder().orderId(orderId).build();

    publish(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(null);
  }
}
