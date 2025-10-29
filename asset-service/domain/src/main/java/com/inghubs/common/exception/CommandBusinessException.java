package com.inghubs.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandBusinessException extends RuntimeException {

  private final String key;
  private final String[] args;

  public CommandBusinessException(String key) {
    super(key);
    this.key = key;
    args = new String[0];
  }

  public CommandBusinessException(String key, String... args) {
    super(key);
    this.key = key;
    this.args = args;
  }
}