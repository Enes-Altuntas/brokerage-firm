package com.inghubs.config;

import com.inghubs.integrator.client.AuthServiceIntegrationClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientProxyConfig {

  @Bean
  public AuthServiceIntegrationClient tfsIntegrationClient(
      @Qualifier("authRestClient") RestClient restClient) {
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(AuthServiceIntegrationClient.class);
  }
}
