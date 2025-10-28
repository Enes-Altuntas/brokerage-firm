package com.inghubs.common.rest.interceptor;

import com.inghubs.common.constant.CommonConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggerInterceptor extends OncePerRequestFilter {

  private static final List<String> EXCLUDED_PATTERNS = List.of(
      "/actuator/**",
      "/swagger-ui/**",
      "/v3/api-docs/**"
  );
  public static final String X_USER_AUTH_HEADER = "x-user-auth-header";
  public static final String AUTHORIZATION = "authorization";

  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return EXCLUDED_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String requestId = request.getHeader(CommonConstant.REQUEST_ID_HEADER);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }

    MDC.put(CommonConstant.MDC_REQUEST_ID, requestId);

    CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
    CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

    wrappedResponse.setHeader(CommonConstant.REQUEST_ID_HEADER, requestId);
    log.info("REST API: [Request Method/URI] : {} {}, [Request Headers] : {}, [Request Body] : {}",
        request.getMethod(),
        request.getRequestURI(),
        getHeadersMap(request),
        new String(wrappedRequest.getCachedBody(), StandardCharsets.UTF_8).replaceAll("\\s+", ""));

    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);

      log.info("REST API: [Response Status] : {}, [Response Headers] : {}",
          response.getStatus(),
          wrappedResponse.getHeaders());

      wrappedResponse.copyBodyToResponse();
    } finally {
      MDC.remove(CommonConstant.MDC_REQUEST_ID);
    }
  }

  private Map<String, String> getHeadersMap(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      if (name.equals(AUTHORIZATION)) {
        continue;
      }
      if (name.equals(X_USER_AUTH_HEADER)) {
        continue;
      }
      headers.put(name, request.getHeader(name));
    }
    return headers;
  }

  public static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
      super(request);
      InputStream requestInputStream = request.getInputStream();
      this.cachedBody = requestInputStream.readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
      return new ServletInputStream() {
        @Override
        public int read() {
          return byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished() {
          return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
      };
    }

    public byte[] getCachedBody() {
      return this.cachedBody;
    }
  }

  public static class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {


    private final Map<String, List<String>> headers = new HashMap<>();
    private ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
      super(response);
    }

    @Override
    public void setHeader(String name, String value) {
      if (value == null) {
        return;
      }

      headers.put(name, new ArrayList<>(List.of(value)));
      super.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
      headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
      super.addHeader(name, value);
    }

    public Map<String, List<String>> getHeaders() {
      return headers;
    }

    @Override
    public ServletOutputStream getOutputStream() {
      if (this.outputStream == null) {
        this.outputStream = new ServletOutputStream() {
          @Override
          public void write(int b) {
            cachedContent.write(b);
          }

          @Override
          public boolean isReady() {
            return true;
          }

          @Override
          public void setWriteListener(WriteListener writeListener) {
          }
        };
      }
      return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() {
      if (this.writer == null) {
        this.writer = new PrintWriter(
            new OutputStreamWriter(cachedContent, StandardCharsets.UTF_8));
      }
      return this.writer;
    }

    public byte[] getCachedContent() {
      return cachedContent.toByteArray();
    }

    public void copyBodyToResponse() throws IOException {
      ServletOutputStream responseOutputStream = super.getOutputStream();
      responseOutputStream.write(getCachedContent());
      responseOutputStream.flush();
    }
  }
}
