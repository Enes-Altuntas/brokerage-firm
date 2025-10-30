package com.inghubs.filter;

import com.inghubs.components.AuthJWTValidator;
import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.GatewayServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCommandFilter extends BaseAuthFilter<OrderCommandFilter.Config> {

  public OrderCommandFilter(AuthJWTValidator authJWTValidator, MessageSource messageSource,
      GatewayServiceProperties gatewayServiceProperties) {
    super(Config.class, authJWTValidator, messageSource, gatewayServiceProperties);
  }

  @Override
  protected boolean isPathSecured(String path) {
    return gatewayServiceProperties.isOrderCommandPathSecured(path);
  }

  @Override
  protected boolean isPathAuthorizedForRoleType(String path, RoleType roleType) {
    return gatewayServiceProperties.isOrderCommandPathAuthorized(path, roleType);
  }

  public static class Config {

  }
}