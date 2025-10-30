package com.inghubs.order.exception;

import lombok.Getter;

@Getter
public class OrderBusinessException extends RuntimeException {

  private final String errorCode;
  private final Object[] params;

  public OrderBusinessException(String errorCode, Object... params) {
    super(errorCode);
    this.errorCode = errorCode;
    this.params = params;
  }
}
