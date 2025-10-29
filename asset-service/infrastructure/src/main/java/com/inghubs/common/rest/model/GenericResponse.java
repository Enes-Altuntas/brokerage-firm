package com.inghubs.common.rest.model;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class GenericResponse<T> implements Serializable {

  private T data;
  private ErrorResponse error;

  public GenericResponse() {
  }

  public GenericResponse(ErrorResponse error) {
    this.error = error;
  }

  public GenericResponse(T data) {
    this.data = data;
  }
}
