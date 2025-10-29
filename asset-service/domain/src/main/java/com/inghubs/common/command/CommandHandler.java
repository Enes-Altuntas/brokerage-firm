package com.inghubs.common.command;

import com.inghubs.common.model.Command;

public interface CommandHandler<R, T extends Command> {

  R handle(T command);
}
