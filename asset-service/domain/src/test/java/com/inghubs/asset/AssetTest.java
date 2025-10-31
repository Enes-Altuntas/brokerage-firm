package com.inghubs.asset;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.inghubs.asset.model.Asset;
import com.inghubs.order.model.Order;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetTest {

  private Asset asset;
  private Order order;

  @BeforeEach
  void setUp() {
    asset = new Asset();
    asset.setUsableSize(new BigDecimal("1000"));

    order = new Order();
    order.setSize(new BigDecimal("10"));
    order.setPrice(new BigDecimal("50"));
  }

  @Test
  void reserveForBuyOrder() {
    asset.reserveForBuyOrder(order);
    assertEquals(new BigDecimal("500"), asset.getUsableSize());
  }

  @Test
  void rollbackForBuyOrder() {
    asset.rollbackForBuyOrder(order);
    assertEquals(new BigDecimal("1500"), asset.getUsableSize());
  }

  @Test
  void reserveForSellOrder() {
    asset.reserveForSellOrder(order);
    assertEquals(new BigDecimal("990"), asset.getUsableSize());
  }

  @Test
  void rollbackForSellOrder() {
    asset.rollbackForSellOrder(order);
    assertEquals(new BigDecimal("1010"), asset.getUsableSize());
  }
}
