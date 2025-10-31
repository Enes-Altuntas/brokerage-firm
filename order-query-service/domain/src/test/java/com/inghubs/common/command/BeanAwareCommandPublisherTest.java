package com.inghubs.common.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inghubs.common.exception.CommandBusinessException;
import com.inghubs.common.model.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeanAwareCommandPublisherTest {

  @InjectMocks
  private BeanAwareCommandPublisher commandPublisher;
  private CommandHandlerRegistry commandHandlerRegistry;

  @BeforeEach
  void setUp() {
    commandHandlerRegistry = CommandHandlerRegistry.INSTANCE;
  }

  @Test
  void shouldPublishCommandAndReturnResult() {
    // Given
    var command = new TestCommand();
    var commandHandler = mock(CommandHandler.class);
    commandHandlerRegistry.register(TestCommand.class, commandHandler);
    when(commandHandler.handle(command)).thenReturn("Result");

    // When
    String result = commandPublisher.publish(String.class, command);

    // Then
    assertEquals("Result", result);
    verify(commandHandler).handle(command);
  }

  @Test
  void shouldThrowExceptionWhenCommandHandlerNotFound() {
    // Given
    var command = new TestCommand();
    commandHandlerRegistry.getRegistryForCommandHandlers().clear();

    // When & Then
    assertThrows(CommandBusinessException.class, () -> commandPublisher.publish(String.class, command));
  }

  private static class TestCommand implements Command {

  }
}