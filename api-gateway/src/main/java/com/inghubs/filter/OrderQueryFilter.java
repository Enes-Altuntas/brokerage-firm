package com.inghubs.filter;

import com.inghubs.components.AuthJWTValidator;
import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.GatewayServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderQueryFilter extends BaseAuthFilter<OrderQueryFilter.Config> {

  public OrderQueryFilter(AuthJWTValidator authJWTValidator, MessageSource messageSource,
      GatewayServiceProperties gatewayServiceProperties) {
    super(Config.class, authJWTValidator, messageSource, gatewayServiceProperties);
  }

  @Override
  protected boolean isPathSecured(String path) {
    return gatewayServiceProperties.isOrderQueryPathSecured(path);
  }

  @Override
  protected boolean isPathAuthorizedForRoleType(String path, RoleType roleType) {
    return gatewayServiceProperties.isOrderQueryPathAuthorized(path, roleType);
  }

  public static class Config {

  }
}