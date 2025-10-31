package com.inghubs.asset.exception;

import lombok.Getter;

@Getter
public class AssetBusinessException extends RuntimeException {

  private final String errorCode;
  private final Object[] params;

  public AssetBusinessException(String errorCode, Object... params) {
    super(errorCode);
    this.errorCode = errorCode;
    this.params = params;
  }
}
