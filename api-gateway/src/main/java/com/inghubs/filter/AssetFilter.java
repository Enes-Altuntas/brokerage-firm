package com.inghubs.filter;

import com.inghubs.components.AuthJWTValidator;
import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.GatewayServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetFilter extends BaseAuthFilter<AssetFilter.Config> {

  public AssetFilter(AuthJWTValidator authJWTValidator, MessageSource messageSource,
      GatewayServiceProperties gatewayServiceProperties) {
    super(Config.class, authJWTValidator, messageSource, gatewayServiceProperties);
  }

  @Override
  protected boolean isPathSecured(String path) {
    return gatewayServiceProperties.isAssetPathSecured(path);
  }

  @Override
  protected boolean isPathAuthorizedForRoleType(String path, RoleType roleType) {
    return gatewayServiceProperties.isAssetPathAuthorized(path, roleType);
  }

  public static class Config {

  }
}