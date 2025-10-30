package com.inghubs.adapters.order.rest.model;

import com.inghubs.order.model.enums.OrderStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderFilterRequest(

    UUID orderId,

    OrderStatus status,

    String assetName,

    UUID customerId

) {

}
