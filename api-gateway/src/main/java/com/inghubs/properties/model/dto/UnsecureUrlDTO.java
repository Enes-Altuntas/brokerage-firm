package com.inghubs.properties.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnsecureUrlDTO {

  @NotNull
  private Boolean exposeUnsecuredUrls;

  @NotNull
  private List<String> unsecuredUrls;

  private Map<String, List<String>> rolePathMapping;

}