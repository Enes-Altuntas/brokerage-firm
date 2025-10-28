package com.inghubs.common.command;

import com.inghubs.common.model.Command;

public interface VoidCommandHandler<T extends Command> {

  void handle(T command);
}
