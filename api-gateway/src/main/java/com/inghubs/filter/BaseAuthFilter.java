package com.inghubs.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.components.AuthJWTValidator;
import com.inghubs.integrator.exception.AuthRestClientException;
import com.inghubs.model.dto.ErrorResponseDTO;
import com.inghubs.model.dto.UserMetadataDTO;
import com.inghubs.model.dto.enums.RoleType;
import com.inghubs.properties.GatewayServiceProperties;
import com.inghubs.utils.LocalResolverUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class BaseAuthFilter<C> extends AbstractGatewayFilterFactory<C> {

  public static final String BEARER = "Bearer";
  public static final String SPACE = " ";

  protected final AuthJWTValidator authJWTValidator;
  protected final MessageSource messageSource;
  protected final GatewayServiceProperties gatewayServiceProperties;

  protected final ObjectMapper objectMapper = new ObjectMapper();

  public BaseAuthFilter(
      Class<C> configClass,
      AuthJWTValidator authJWTValidator, MessageSource messageSource,
      GatewayServiceProperties gatewayServiceProperties) {
    super(configClass);
    this.authJWTValidator = authJWTValidator;
    this.messageSource = messageSource;
    this.gatewayServiceProperties = gatewayServiceProperties;
  }

  @Override
  public GatewayFilter apply(C config) {
    return (exchange, chain) -> {

      String path = exchange.getRequest().getURI().getPath();
      log.info("Auth Filter path: {}", path);

      boolean isSecured = isPathSecured(path);
      if (!isSecured) {
        log.info("Auth Filter path is not secured");
        return chain.filter(exchange);
      }

      final List<String> authHeaders = exchange.getRequest().getHeaders()
          .get(HttpHeaders.AUTHORIZATION);
      if (authHeaders == null || authHeaders.isEmpty()) {
        log.info("Auth Filter header is empty");
        return onError(exchange, HttpStatus.UNAUTHORIZED.value());
      }

      final String authHeader = authHeaders.getFirst();
      final String[] parts = authHeader.split(SPACE);
      if (parts.length != 2 || !BEARER.equals(parts[0])) {
        log.info("Auth Filter header is invalid");
        return onError(exchange, HttpStatus.BAD_REQUEST.value());
      }

      try {

        log.info("Auth Filter header is valid");
        final UserMetadataDTO userMetadataDTO = authJWTValidator.validateToken(authHeader);
        log.info("Token parsed: {}", userMetadataDTO);

        final RoleType userProductRole = userMetadataDTO.getRole();
        if (userProductRole == null) {
          log.info("User product role is null for path: {}", path);
          return onError(exchange, HttpStatus.FORBIDDEN.value());
        }

        if (!isPathAuthorizedForRoleType(path, userProductRole)) {
          log.info("User product role is not authorized for path: {}", path);
          return onError(exchange, HttpStatus.FORBIDDEN.value());
        }

        log.info("User is authorized for path: {}", path);
        Builder mutatedRequest = exchange.getRequest().mutate()
            .header("x-customer-id", userMetadataDTO.getId())
            .header("x-customer-role", userMetadataDTO.getRole().name());

        List<String> clientIpHeaders = exchange.getRequest().getHeaders().get("X-Forwarded-For");
        if (clientIpHeaders != null && !clientIpHeaders.isEmpty()) {
          mutatedRequest.header("x-client-ip", clientIpHeaders.getFirst());
        }

        List<String> acceptLanguageHeaders = exchange.getRequest().getHeaders()
            .get("accept-language");
        if (acceptLanguageHeaders != null && !acceptLanguageHeaders.isEmpty()) {
          mutatedRequest.header("x-accept-language", acceptLanguageHeaders.getFirst());
        }

        return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
      } catch (AuthRestClientException e) {
        log.error("Error in auth service integration", e);
        return onAuthServiceError(exchange, e);
      } catch (Exception e) {
        log.error("Error in AuthFilter", e);
        return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR.value());
      }
    };
  }

  protected Mono<Void> onError(ServerWebExchange exchange, Integer httpStatusCode) {
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    exchange.getResponse().setStatusCode(HttpStatus.valueOf(httpStatusCode));

    try {
      ObjectMapper mapper = new ObjectMapper();
      ErrorResponseDTO errorResponse = new ErrorResponseDTO(httpStatusCode.toString(),
          messageSource.getMessage(httpStatusCode.toString(), null,
              LocalResolverUtil.resolveLocale(exchange)));

      String body = mapper.writeValueAsString(errorResponse);

      DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
      DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
      return exchange.getResponse().writeWith(Mono.just(buffer));
    } catch (Exception e) {
      return Mono.error(e);
    }
  }

  public Mono<Void> onAuthServiceError(ServerWebExchange exchange, AuthRestClientException ex) {
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    exchange.getResponse().setStatusCode(HttpStatus.valueOf(ex.getHttpStatusCode()));

    try {
      ObjectMapper mapper = new ObjectMapper();
      String body = mapper.writeValueAsString(ex.getErrorResponse());

      DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
      DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
      return exchange.getResponse().writeWith(Mono.just(buffer));
    } catch (Exception e) {
      return Mono.error(e);
    }
  }

  protected abstract boolean isPathSecured(String path);

  protected abstract boolean isPathAuthorizedForRoleType(String path,
      RoleType productRole);
}