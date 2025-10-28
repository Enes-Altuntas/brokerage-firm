package com.inghubs.common.command;

import com.inghubs.common.model.Command;

public interface CommandPublisher {

  <R, T extends Command> R publish(Class<R> returnClass, T command);

  <R, T extends Command> void publish(T command);

  <R> R publish(Class<R> returnClass);
}
