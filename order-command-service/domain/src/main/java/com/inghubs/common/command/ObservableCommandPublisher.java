package com.inghubs.common.command;

import com.inghubs.common.model.Command;

public class ObservableCommandPublisher extends BeanAwareCommandPublisher {

  public <R, T extends Command> void register(Class<T> commandClass,
      CommandHandler<R, T> commandHandler) {
    CommandHandlerRegistry.INSTANCE.register(commandClass, commandHandler);
  }

  public <T extends Command> void register(Class<T> commandClass,
      VoidCommandHandler<T> commandHandler) {
    CommandHandlerRegistry.INSTANCE.register(commandClass, commandHandler);
  }

  public <R> void register(Class<R> returnClass, NoCommandHandler<R> commandHandler) {
    CommandHandlerRegistry.INSTANCE.register(returnClass, commandHandler);
  }
}
