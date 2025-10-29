package com.inghubs.common.locale;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.LocaleResolver;

@Component
public class LocaleUtil {

  private final LocaleResolver localeResolver;

  public LocaleUtil(LocaleResolver localeResolver) {
    this.localeResolver = localeResolver;
  }

  public Locale getCurrentLocale() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(
          RequestAttributes.REFERENCE_REQUEST);
      if (request != null) {
        return localeResolver.resolveLocale(request);
      }
    }

    return Locale.forLanguageTag("tr");
  }
}
