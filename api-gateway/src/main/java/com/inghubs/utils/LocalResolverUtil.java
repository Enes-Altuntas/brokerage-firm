package com.inghubs.utils;

import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.springframework.web.server.ServerWebExchange;

@UtilityClass
public class LocalResolverUtil {

  private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("tr");

  public Locale resolveLocale(ServerWebExchange exchange) {
    String lang = exchange.getRequest().getHeaders().getFirst("Accept-Language");
    if (lang != null && !lang.isBlank()) {
      return Locale.forLanguageTag(lang);
    }
    return DEFAULT_LOCALE;
  }

}
