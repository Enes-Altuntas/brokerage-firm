package com.inghubs.integrator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.integrator.exception.AuthRestClientException;
import com.inghubs.integrator.model.AuthServiceErrorResponse;
import com.inghubs.interceptor.ClientLoggerRequestInterceptor;
import com.inghubs.properties.GatewayServiceProperties;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthRestClientConfig {

  private final ClientLoggerRequestInterceptor clientLoggerRequestInterceptor;
  private final GatewayServiceProperties gatewayServiceProperties;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Bean(name = "authRestClient")
  public RestClient authRestClient() {
    return RestClient.builder()
        .baseUrl(gatewayServiceProperties.getTokenValidationEndpoint())
        .requestInterceptor(clientLoggerRequestInterceptor)
        .defaultStatusHandler(HttpStatusCode::isError, (req, res) -> {
          String resBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
          log.error(resBody);

          AuthServiceErrorResponse errorResponse = objectMapper.readValue(resBody,
              AuthServiceErrorResponse.class);

          throw new AuthRestClientException(res.getStatusCode().value(), errorResponse);
        })
        .build();
  }
}
