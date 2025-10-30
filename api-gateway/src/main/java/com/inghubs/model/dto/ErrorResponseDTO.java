package com.inghubs.model.dto;

import com.inghubs.integrator.model.FieldValidationResponseDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorResponseDTO {

  private String errorCode;

  private String errorMessage;

  private List<FieldValidationResponseDTO> validationFields;

  private String timestamp;

  public ErrorResponseDTO(String errorCode, String errorMessage) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
    this.timestamp = LocalDateTime.now().toString();
    this.validationFields = new ArrayList<>();
  }
}
