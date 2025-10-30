package com.inghubs.common.exception;

import lombok.Getter;

@Getter
public class RedisLockException extends RuntimeException {

  private final String errorCode;
  private final Object[] params;

  public RedisLockException(String errorCode, Object... params) {
    super(errorCode);
    this.errorCode = errorCode;
    this.params = params;
  }
}
