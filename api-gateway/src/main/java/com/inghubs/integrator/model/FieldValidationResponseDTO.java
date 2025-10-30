package com.inghubs.integrator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldValidationResponseDTO {

  private String field;

  private String message;

}
