package com.inghubs.filter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

  private static final String START_TIME_KEY = "startTime";
  private static final String REQUEST_ID_KEY = "x-request-id";
  private static final String X_USER_AUTH_HEADER = "x-user-auth-header";
  private static final String AUTHORIZATION = "authorization";

  private static final List<String> EXCLUDED_PATTERNS = List.of(
      "/actuator",
      "/swagger-ui",
      "/v3/api-docs"
  );

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (shouldNotFilter(request.getPath().value())) {
      return chain.filter(exchange);
    }

    String requestId = setupContext(request, exchange);

    log.info(">>>> REQUEST START | ID: {} | Method/URI: {} {} | Headers: {}",
        requestId,
        request.getMethod(),
        request.getURI().getPath(),
        getFilteredHeaders(request.getHeaders()));

    ServerHttpRequest decoratedRequest = request.mutate()
        .header(REQUEST_ID_KEY, requestId)
        .build();

    ServerWebExchange mutatedExchange = exchange.mutate().request(decoratedRequest).build();

    return chain.filter(mutatedExchange).doFinally(signalType -> {

      Instant endTime = Instant.now();
      Instant startTime = exchange.getAttribute(START_TIME_KEY);
      long durationMs = startTime != null ? endTime.toEpochMilli() - startTime.toEpochMilli() : -1;

      Integer httpStatus = exchange.getResponse().getStatusCode() != null
          ? exchange.getResponse().getStatusCode().value()
          : 500;

      log.info("<<<< REQUEST END | ID: {} | Status: {} | Duration: {}ms | Response Headers Count: {}",
          requestId,
          httpStatus,
          durationMs,
          exchange.getResponse().getHeaders().size()
      );

      MDC.remove(REQUEST_ID_KEY);
    });
  }

  private String setupContext(ServerHttpRequest request, ServerWebExchange exchange) {
    String requestId = request.getHeaders().getFirst(REQUEST_ID_KEY);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }
    MDC.put(REQUEST_ID_KEY, requestId);
    exchange.getResponse().getHeaders().set(REQUEST_ID_KEY, requestId);
    exchange.getAttributes().put(START_TIME_KEY, Instant.now());
    return requestId;
  }

  private boolean shouldNotFilter(String path) {
    return EXCLUDED_PATTERNS.stream().anyMatch(path::startsWith);
  }

  private Map<String, String> getFilteredHeaders(HttpHeaders headers) {
    return headers.entrySet().stream()
        .filter(entry -> !entry.getKey().equalsIgnoreCase(AUTHORIZATION) &&
            !entry.getKey().equalsIgnoreCase(X_USER_AUTH_HEADER))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> String.join(", ", entry.getValue())
        ));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}