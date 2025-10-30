package com.inghubs.interceptor;

import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientLoggerRequestInterceptor implements ClientHttpRequestInterceptor {

  @NotNull
  @Override
  public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body,
      ClientHttpRequestExecution execution)
      throws IOException {
    logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    return logResponse(request, response);
  }

  private void logRequest(HttpRequest request, byte[] body) {
    log.info(
        "CLIENT API: [Request Method/URI] : {} {}, [Request Headers] : {}, [Request Body] : {}",
        request.getMethod(),
        request.getURI(),
        getRequestHeaders(request.getHeaders()),
        new String(body, StandardCharsets.UTF_8).replaceAll("\\s+", ""));
  }

  private ClientHttpResponse logResponse(HttpRequest request, ClientHttpResponse response)
      throws IOException {
    byte[] responseBody = response.getBody().readAllBytes();

    log.info("CLIENT API: [Response Status] : {}, [Response Headers] : {}, [Response Body] : {}",
        response.getStatusCode(),
        getResponseHeaders(response.getHeaders()),
        new String(responseBody, StandardCharsets.UTF_8).replaceAll("\\s+", ""));

    return new BufferingClientHttpResponseWrapper(response, responseBody);
  }

  private String getRequestHeaders(HttpHeaders headers) {
    Set<String> ignoredHeaders = Set.of("Authorization", "Cookie", "X-Client-Certificate",
        "X-JWS-Signature");

    return headers.entrySet().stream()
        .filter(entry -> !ignoredHeaders.contains(entry.getKey()))
        .flatMap(entry -> entry.getValue().stream()
            .map(value -> entry.getKey() + "=" + value))
        .collect(java.util.stream.Collectors.joining(", "));
  }

  private String getResponseHeaders(HttpHeaders headers) {
    Set<String> ignoredHeaders = Set.of("Authorization", "Cookie", "X-Client-Certificate",
        "X-JWS-Signature");

    return headers.entrySet().stream()
        .filter(entry -> !ignoredHeaders.contains(entry.getKey()))
        .flatMap(entry -> entry.getValue().stream()
            .map(value -> entry.getKey() + "=" + value))
        .collect(java.util.stream.Collectors.joining(", "));
  }


  private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private final byte[] body;

    public BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
      this.response = response;
      this.body = body;
    }

    @NotNull
    @Override
    public InputStream getBody() throws IOException {
      return new ByteArrayInputStream(body);
    }

    @NotNull
    @Override
    public HttpHeaders getHeaders() {
      return response.getHeaders();
    }

    @NotNull
    @Override
    public HttpStatusCode getStatusCode() throws IOException {
      return response.getStatusCode();
    }

    @NotNull
    @Override
    public String getStatusText() throws IOException {
      return response.getStatusText();
    }

    @Override
    public void close() {
      response.close();
    }
  }
}