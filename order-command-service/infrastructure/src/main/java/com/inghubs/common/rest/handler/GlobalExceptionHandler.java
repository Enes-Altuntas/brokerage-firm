package com.inghubs.common.rest.handler;

import com.inghubs.common.exception.RedisLockException;
import com.inghubs.common.locale.LocaleUtil;
import com.inghubs.common.rest.base.BaseController;
import com.inghubs.common.rest.model.ErrorResponse;
import com.inghubs.common.rest.model.FieldValidationResponse;
import com.inghubs.common.rest.model.GenericResponse;
import com.inghubs.order.exception.OrderBusinessException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends BaseController {

  private final LocaleUtil localeUtil;
  private final MessageSource messageSource;

  @ExceptionHandler(RedisLockException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleRedisLockException(
      RedisLockException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse(ex.getErrorCode(),
        getMessageFromSourceWithLocale(ex.getErrorCode(), ex.getParams()));

    return ResponseEntity.badRequest().body(respond(error));
  }

  @ExceptionHandler(OrderBusinessException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleOrderBusinessException(
      OrderBusinessException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse(ex.getErrorCode(),
        getMessageFromSourceWithLocale(ex.getErrorCode(), ex.getParams()));

    return ResponseEntity.badRequest().body(respond(error));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse("1001",
        getMessageFromSourceWithLocale("1001", null));

    return ResponseEntity.badRequest().body(respond(error));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse("1001",
        getMessageFromSourceWithLocale("1001", null));

    return ResponseEntity.badRequest().body(respond(error));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse("1001",
        getMessageFromSourceWithLocale("1001", new Object[]{ex.getMessage()}));
    List<FieldValidationResponse> fieldValidationDTO = getFieldValidationMessagesFromSourceWithLocale(
        ex);
    error.setFieldErrors(fieldValidationDTO);

    return ResponseEntity.badRequest().body(respond(error));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleMissingRequestHeaderException(
      MissingRequestHeaderException ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse("1001",
        getMessageFromSourceWithLocale("1002", new Object[]{ex.getHeaderName()}));

    return ResponseEntity.internalServerError().body(respond(error));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericResponse<ErrorResponse>> handleGlobalException(Exception ex, WebRequest webRequest) {
    log.error("Exception occurred on request: {}", webRequest.getDescription(false), ex);

    ErrorResponse error = new ErrorResponse("1000",
        getMessageFromSourceWithLocale("1000", null));

    return ResponseEntity.internalServerError().body(respond(error));
  }

  private String getMessageFromSourceWithLocale(String errorCode, Object[] params) {
    Locale currentLocale = localeUtil.getCurrentLocale();
    return messageSource.getMessage(errorCode, params, currentLocale);
  }

  private List<FieldValidationResponse> getFieldValidationMessagesFromSourceWithLocale(
      MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().stream().map(fieldError -> {
      Object[] arguments = fieldError.getArguments();

      if (arguments == null || arguments.length == 0) {
        return null;
      }

      String fieldMessage = fieldError.getDefaultMessage();
      Object[] fieldErrorValue = null;
      if (arguments.length == 2) {
        fieldErrorValue = new Object[]{arguments[1]};
      }

      return new FieldValidationResponse(fieldError.getField(),
          getMessageFromSourceWithLocale(fieldMessage, fieldErrorValue));
    }).collect(Collectors.toList());
  }
}
