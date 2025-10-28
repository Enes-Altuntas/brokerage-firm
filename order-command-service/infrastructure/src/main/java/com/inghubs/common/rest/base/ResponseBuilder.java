package com.inghubs.common.rest.base;

import com.inghubs.common.rest.model.ErrorResponse;
import com.inghubs.common.rest.model.GenericResponse;

public class ResponseBuilder {

  private ResponseBuilder() {
  }

  public static <T> GenericResponse<T> build(T item) {
    return new GenericResponse<>(item);
  }

  public static GenericResponse<ErrorResponse> build(ErrorResponse errorResponse) {
    return new GenericResponse<>(errorResponse);
  }
}