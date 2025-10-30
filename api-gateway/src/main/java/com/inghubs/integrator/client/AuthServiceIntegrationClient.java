package com.inghubs.integrator.client;

import com.inghubs.model.dto.UserMetadataDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1")
public interface AuthServiceIntegrationClient {

  @GetExchange("/user/account")
  UserMetadataDTO checkToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

}