package com.inghubs.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.inghubs.common.model.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObservableCommandPublisherTest {

  @InjectMocks
  private ObservableCommandPublisher observableCommandPublisher;
  private CommandHandlerRegistry commandHandlerRegistry;

  @BeforeEach
  void setUp() {
    commandHandlerRegistry = CommandHandlerRegistry.INSTANCE;
    commandHandlerRegistry.getRegistryForCommandHandlers().clear();
    commandHandlerRegistry.getRegistryForVoidCommandHandlers().clear();
    commandHandlerRegistry.getRegistryForNoCommandHandlers().clear();
  }

  @Test
  void shouldRegisterCommandHandler() {
    // Given
    var commandHandler = mock(CommandHandler.class);

    // When
    observableCommandPublisher.register(TestCommand.class, commandHandler);

    // Then
    var detectedHandler = commandHandlerRegistry.detectCommandHandlerFrom(TestCommand.class);
    assertNotNull(detectedHandler);
    assertEquals(commandHandler, detectedHandler);
  }

  @Test
  void shouldRegisterVoidCommandHandler() {
    // Given
    var voidCommandHandler = mock(VoidCommandHandler.class);

    // When
    observableCommandPublisher.register(TestCommand.class, voidCommandHandler);

    // Then
    var detectedHandler = commandHandlerRegistry.detectVoidCommandHandlerFrom(TestCommand.class);
    assertNotNull(detectedHandler);
    assertEquals(voidCommandHandler, detectedHandler);
  }

  @Test
  void shouldRegisterNoCommandHandler() {
    // Given
    var noCommandHandler = mock(NoCommandHandler.class);

    // When
    observableCommandPublisher.register(String.class, noCommandHandler);

    // Then
    var detectedHandler = commandHandlerRegistry.detectNoCommandHandlerFrom(String.class);
    assertNotNull(detectedHandler);
    assertEquals(noCommandHandler, detectedHandler);
  }

  private static class TestCommand implements Command {

  }
}