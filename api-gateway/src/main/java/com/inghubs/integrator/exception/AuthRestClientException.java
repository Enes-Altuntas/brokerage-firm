package com.inghubs.integrator.exception;

import com.inghubs.integrator.model.AuthServiceErrorResponse;
import lombok.Getter;

@Getter
public class AuthRestClientException extends RuntimeException {

  private final Integer httpStatusCode;
  private final AuthServiceErrorResponse errorResponse;

  public AuthRestClientException(Integer httpStatusCode, AuthServiceErrorResponse errorResponse) {
    this.httpStatusCode = httpStatusCode;
    this.errorResponse = errorResponse;
  }
}
