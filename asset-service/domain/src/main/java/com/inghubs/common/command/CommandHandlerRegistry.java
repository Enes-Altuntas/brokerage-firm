package com.inghubs.common.command;

import com.inghubs.common.model.Command;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CommandHandlerRegistry {

  public static final CommandHandlerRegistry INSTANCE = new CommandHandlerRegistry();
  private Map<Class<? extends Command>, CommandHandler<?, ? extends Command>> registryForCommandHandlers;
  private Map<Class<? extends Command>, VoidCommandHandler<? extends Command>> registryForVoidCommandHandlers;
  private Map<Class<?>, NoCommandHandler<?>> registryForNoCommandHandlers;

  private CommandHandlerRegistry() {
    registryForCommandHandlers = new HashMap<>();
    registryForVoidCommandHandlers = new HashMap<>();
    registryForNoCommandHandlers = new HashMap<>();
  }

  public <R, T extends Command> void register(Class<T> key, CommandHandler<R, T> commandHandler) {
    log.info("Command {} is registered by handler {}", key.getSimpleName(),
        commandHandler.getClass().getSimpleName());
    registryForCommandHandlers.put(key, commandHandler);
  }

  public <T extends Command> void register(Class<T> key, VoidCommandHandler<T> commandHandler) {
    log.info("Command {} is registered by void handler {}", key.getSimpleName(),
        commandHandler.getClass().getSimpleName());
    registryForVoidCommandHandlers.put(key, commandHandler);
  }

  public <R> void register(Class<R> key, NoCommandHandler<R> commandHandler) {
    log.info("Command {} is registered by no param handler {}", key.getSimpleName(),
        commandHandler.getClass().getSimpleName());
    registryForNoCommandHandlers.put(key, commandHandler);
  }

  public CommandHandler<?, ? extends Command> detectCommandHandlerFrom(
      Class<? extends Command> commandHandler) {
    return registryForCommandHandlers.get(commandHandler);
  }

  public VoidCommandHandler<? extends Command> detectVoidCommandHandlerFrom(
      Class<? extends Command> commandHandler) {
    return registryForVoidCommandHandlers.get(commandHandler);
  }

  public NoCommandHandler<?> detectNoCommandHandlerFrom(Class<?> returnClass) {
    return registryForNoCommandHandlers.get(returnClass);
  }
}
