package com.inghubs.common.command;

import com.inghubs.common.annotation.DomainComponent;
import com.inghubs.common.exception.CommandBusinessException;
import com.inghubs.common.model.Command;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainComponent
public class BeanAwareCommandPublisher implements CommandPublisher {

  @Override
  @SuppressWarnings("unchecked")
  public <R, T extends Command> R publish(Class<R> returnClass, T command) {
    var commandHandler = (CommandHandler<R, T>) CommandHandlerRegistry.INSTANCE.detectCommandHandlerFrom(
        command.getClass());
    validateCommandHandlerDetection(command, commandHandler);
    return commandHandler.handle(command);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R, T extends Command> void publish(T command) {
    var voidCommandHandler = (VoidCommandHandler<T>) CommandHandlerRegistry.INSTANCE.detectVoidCommandHandlerFrom(
        command.getClass());
    if (Objects.isNull(voidCommandHandler)) {
      var commandHandler = (CommandHandler<R, T>) CommandHandlerRegistry.INSTANCE.detectCommandHandlerFrom(
          command.getClass());
      validateCommandHandlerDetection(command, commandHandler);
      commandHandler.handle(command);
    } else {
      validateVoidCommandHandlerDetection(command, voidCommandHandler);
      voidCommandHandler.handle(command);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R publish(Class<R> returnClass) {
    var commandHandler = (NoCommandHandler<R>) CommandHandlerRegistry.INSTANCE.detectNoCommandHandlerFrom(
        returnClass);
    validateNoParamCommandHandlerDetection(commandHandler);
    return commandHandler.handle();
  }

  private <R, T extends Command> void validateCommandHandlerDetection(T command,
      CommandHandler<R, T> commandHandler) {
    if (Objects.isNull(commandHandler)) {
      log.error("Use case handler cannot be detected for the use case: {}, handlers: {}", command,
          CommandHandlerRegistry.INSTANCE.getRegistryForCommandHandlers());
      throw new CommandBusinessException("paymentapi.commandHandler.notDetected");
    }
  }

  private <T extends Command> void validateVoidCommandHandlerDetection(T command,
      VoidCommandHandler<T> commandHandler) {
    if (Objects.isNull(commandHandler)) {
      log.error("Void use case handler cannot be detected for the use case: {}, handlers: {}",
          command, CommandHandlerRegistry.INSTANCE.getRegistryForVoidCommandHandlers());
      throw new CommandBusinessException("paymentapi.commandHandler.notDetected");
    }
  }

  private <R> void validateNoParamCommandHandlerDetection(NoCommandHandler<R> commandHandler) {
    if (Objects.isNull(commandHandler)) {
      log.error("Void use case handler cannot be detected for the handlers: {}",
          CommandHandlerRegistry.INSTANCE.getRegistryForNoCommandHandlers());
      throw new CommandBusinessException("paymentapi.commandHandler.notDetected");
    }
  }
}
