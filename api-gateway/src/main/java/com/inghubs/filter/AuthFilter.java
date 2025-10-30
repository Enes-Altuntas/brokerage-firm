package com.inghubs.filter;

import com.inghubs.components.AuthJWTValidator;
import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.GatewayServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthFilter extends BaseAuthFilter<AuthFilter.Config> {

  public AuthFilter(AuthJWTValidator authJWTValidator, MessageSource messageSource,
      GatewayServiceProperties gatewayServiceProperties) {
    super(Config.class, authJWTValidator, messageSource, gatewayServiceProperties);
  }

  @Override
  protected boolean isPathSecured(String path) {
    return gatewayServiceProperties.isAuthPathSecured(path);
  }

  @Override
  protected boolean isPathAuthorizedForRoleType(String path, RoleType roleType) {
    return gatewayServiceProperties.isAuthPathAuthorized(path, roleType);
  }

  public static class Config {

  }
}