package com.inghubs.properties;

import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.model.dto.UnsecureUrlDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gateway.service.properties")
public class GatewayServiceProperties {

  @NotNull
  private UnsecureUrlDTO assetService;

  @NotNull
  private UnsecureUrlDTO orderCommandService;

  @NotNull
  private UnsecureUrlDTO orderQueryService;

  @NotNull
  private UnsecureUrlDTO authService;

  @NotNull
  private String tokenValidationEndpoint;

  public boolean isAssetPathSecured(String path) {
    return isSecured(path, assetService);
  }

  public boolean isOrderCommandPathSecured(String path) {
    return isSecured(path, orderCommandService);
  }

  public boolean isOrderQueryPathSecured(String path) {
    return isSecured(path, orderQueryService);
  }

  public boolean isAuthPathSecured(String path) {
    return isSecured(path, authService);
  }

  public boolean isAssetPathAuthorized(String path, RoleType roleType) {
    return isAuthorized(path, roleType, assetService);
  }

  public boolean isOrderQueryPathAuthorized(String path, RoleType roleType) {
    return isAuthorized(path, roleType, orderQueryService);
  }

  public boolean isAuthPathAuthorized(String path, RoleType roleType) {
    return isAuthorized(path, roleType, authService);
  }

  public boolean isOrderCommandPathAuthorized(String path, RoleType roleType) {
    return isAuthorized(path, roleType, orderCommandService);
  }

  private boolean isAuthorized(String path, RoleType roleType,
      UnsecureUrlDTO unsecureUrlDTO) {
    Map<String, List<String>> rolePathMapping = unsecureUrlDTO.getRolePathMapping();

    if (rolePathMapping == null || rolePathMapping.isEmpty()) {
      return true;
    }

    List<String> patterns = rolePathMapping.get(roleType.toString());
    if (patterns == null || patterns.isEmpty()) {
      return false;
    }

    AntPathMatcher matcher = new AntPathMatcher();

    return patterns.stream().anyMatch(pattern -> matcher.match(pattern, path));
  }

  private boolean isSecured(String path, UnsecureUrlDTO unsecureUrlDTO) {
    if (!unsecureUrlDTO.getExposeUnsecuredUrls()) {
      return true;
    }

    final var pathMatcher = new AntPathMatcher();
    for (final var pattern : unsecureUrlDTO.getUnsecuredUrls()) {
      if (pathMatcher.match(pattern, path)) {
        return false;
      }
    }

    return true;
  }
}