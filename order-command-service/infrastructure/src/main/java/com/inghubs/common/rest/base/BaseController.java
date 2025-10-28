package com.inghubs.common.rest.base;

import com.inghubs.common.command.BeanAwareCommandPublisher;
import com.inghubs.common.rest.model.ErrorResponse;
import com.inghubs.common.rest.model.GenericResponse;

public class BaseController extends BeanAwareCommandPublisher {

  protected <T> GenericResponse<T> respond(T item) {
    return ResponseBuilder.build(item);
  }

  protected GenericResponse<ErrorResponse> respond(ErrorResponse errorResponse) {
    return ResponseBuilder.build(errorResponse);
  }

  protected GenericResponse<Void> respond() {
    return ResponseBuilder.build((Void) null);
  }
}
