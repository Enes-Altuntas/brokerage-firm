package com.inghubs.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import com.inghubs.common.model.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommandHandlerRegistryTest {

  private CommandHandlerRegistry commandHandlerRegistry;

  @BeforeEach
  void setUp() {
    commandHandlerRegistry = CommandHandlerRegistry.INSTANCE;
    commandHandlerRegistry.getRegistryForCommandHandlers().clear();
    commandHandlerRegistry.getRegistryForVoidCommandHandlers().clear();
    commandHandlerRegistry.getRegistryForNoCommandHandlers().clear();
  }

  @Test
  void shouldRegisterAndDetectCommandHandler() {
    // Given
    var commandHandler = mock(CommandHandler.class);
    commandHandlerRegistry.register(TestCommand.class, commandHandler);

    // When
    var detectedHandler = commandHandlerRegistry.detectCommandHandlerFrom(TestCommand.class);

    // Then
    assertNotNull(detectedHandler);
    assertEquals(commandHandler, detectedHandler);
  }

  @Test
  void shouldRegisterAndDetectVoidCommandHandler() {
    // Given
    var voidCommandHandler = mock(VoidCommandHandler.class);
    commandHandlerRegistry.register(TestCommand.class, voidCommandHandler);

    // When
    var detectedHandler = commandHandlerRegistry.detectVoidCommandHandlerFrom(TestCommand.class);

    // Then
    assertNotNull(detectedHandler);
    assertEquals(voidCommandHandler, detectedHandler);
  }

  @Test
  void shouldRegisterAndDetectNoCommandHandler() {
    // Given
    var noCommandHandler = mock(NoCommandHandler.class);
    commandHandlerRegistry.register(String.class, noCommandHandler);

    // When
    var detectedHandler = commandHandlerRegistry.detectNoCommandHandlerFrom(String.class);

    // Then
    assertNotNull(detectedHandler);
    assertEquals(noCommandHandler, detectedHandler);
  }

  @Test
  void shouldReturnNullWhenCommandHandlerNotFound() {
    // When
    var detectedHandler = commandHandlerRegistry.detectCommandHandlerFrom(TestCommand.class);

    // Then
    assertNull(detectedHandler);
  }

  private static class TestCommand implements Command {

  }
}