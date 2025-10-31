package com.inghubs.asset.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.inghubs.order.model.Order;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetTest {

  private Asset asset;

  @BeforeEach
  void setUp() {
    asset = Asset.builder()
        .usableSize(new BigDecimal("1000"))
        .build();
  }

  @Test
  void reserveForBuyOrder() {
    Order order = Order.builder()
        .size(new BigDecimal("10"))
        .price(new BigDecimal("50"))
        .build();

    asset.reserveForBuyOrder(order);

    assertEquals(new BigDecimal("500"), asset.getUsableSize());
  }

  @Test
  void rollbackForBuyOrder() {
    Order order = Order.builder()
        .size(new BigDecimal("10"))
        .price(new BigDecimal("50"))
        .build();

    asset.rollbackForBuyOrder(order);

    assertEquals(new BigDecimal("1500"), asset.getUsableSize());
  }

  @Test
  void reserveForSellOrder() {
    Order order = Order.builder()
        .size(new BigDecimal("100"))
        .build();

    asset.reserveForSellOrder(order);

    assertEquals(new BigDecimal("900"), asset.getUsableSize());
  }

  @Test
  void rollbackForSellOrder() {
    Order order = Order.builder()
        .size(new BigDecimal("100"))
        .build();

    asset.rollbackForSellOrder(order);

    assertEquals(new BigDecimal("1100"), asset.getUsableSize());
  }
}
