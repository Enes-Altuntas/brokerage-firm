package com.inghubs.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommandBusinessExceptionTest {

  @Test
  void shouldCreateExceptionWithKey() {
    // Given
    String key = "error.key";

    // When
    CommandBusinessException exception = new CommandBusinessException(key);

    // Then
    assertEquals(key, exception.getKey());
    assertNotNull(exception.getArgs());
    assertEquals(0, exception.getArgs().length);
  }

  @Test
  void shouldCreateExceptionWithKeyAndArgs() {
    // Given
    String key = "error.key";
    String[] args = {"arg1", "arg2"};

    // When
    CommandBusinessException exception = new CommandBusinessException(key, args);

    // Then
    assertEquals(key, exception.getKey());
    assertEquals(args, exception.getArgs());
  }
}