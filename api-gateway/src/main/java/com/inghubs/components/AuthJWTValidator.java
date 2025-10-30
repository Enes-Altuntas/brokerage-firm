package com.inghubs.components;

import com.inghubs.integrator.client.AuthServiceIntegrationClient;
import com.inghubs.model.dto.UserMetadataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthJWTValidator {

  private final AuthServiceIntegrationClient restClient;

  public UserMetadataDTO validateToken(String authHeader) {
    return restClient.checkToken(authHeader);
  }
}