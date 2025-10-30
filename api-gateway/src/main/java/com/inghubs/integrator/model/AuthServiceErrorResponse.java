package com.inghubs.integrator.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthServiceErrorResponse {

  private String errorCode;

  private String errorMessage;

  private List<FieldValidationResponseDTO> validationFields;

  private String timestamp;

}
