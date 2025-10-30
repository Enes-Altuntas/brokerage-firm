package com.inghubs.adapters.order.elastic.document;

import com.inghubs.order.model.Order;
import com.inghubs.order.model.enums.OrderSide;
import com.inghubs.order.model.enums.OrderStatus;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "orders")
public class OrderDocument {

  @Id
  private UUID id;

  @Field(type = FieldType.Keyword)
  private UUID customerId;

  @Field(type = FieldType.Keyword)
  private String assetName;

  @Field(type = FieldType.Keyword)
  private OrderSide side;

  @Field(type = FieldType.Double)
  private BigDecimal size;

  @Field(type = FieldType.Double)
  private BigDecimal price;

  @Field(type = FieldType.Keyword)
  private OrderStatus status;

  @Field(type = FieldType.Date)
  private Instant createdAt;

  @Field(type = FieldType.Date)
  private Instant updatedAt;

  @Field(type = FieldType.Date)
  private Instant deletedAt;

  @Field(type = FieldType.Keyword)
  private String createdBy;

  @Field(type = FieldType.Keyword)
  private String updatedBy;

  public OrderDocument(Order order) {
    this.id = order.getId();
    this.customerId = order.getCustomerId();
    this.assetName = order.getAssetName();
    this.side = order.getSide();
    this.size = order.getSize();
    this.price = order.getPrice();
    this.status = order.getStatus();
    this.createdAt = order.getCreatedAt();
    this.updatedAt = order.getUpdatedAt();
    this.deletedAt = order.getDeletedAt();
    this.createdBy = order.getCreatedBy();
    this.updatedBy = order.getUpdatedBy();
  }

  public Order toDomain() {
    return Order.builder()
        .id(id)
        .customerId(customerId)
        .assetName(assetName)
        .side(side)
        .size(size)
        .price(price)
        .status(status)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .deletedAt(deletedAt)
        .createdBy(createdBy)
        .updatedBy(updatedBy)
        .build();
  }
}
