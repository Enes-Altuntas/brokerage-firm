package com.inghubs.common.rest.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
  private String errorCode;
  private String errorDescription;
  @Builder.Default
  private List<FieldValidationResponse> fieldErrors = new ArrayList<>();
  private String timestamp;

  public ErrorResponse(String errorCode, String errorDescription) {
    this.errorDescription = errorDescription;
    this.errorCode = errorCode;
    this.timestamp = LocalDateTime.now().toString();
    this.fieldErrors = new ArrayList<>();
  }
}
