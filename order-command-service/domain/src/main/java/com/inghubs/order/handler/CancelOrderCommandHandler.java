package com.inghubs.order.handler;

import com.inghubs.common.command.ObservableCommandPublisher;
import com.inghubs.common.command.VoidCommandHandler;
import com.inghubs.order.command.CancelOrderCommand;
import org.springframework.stereotype.Service;

@Service
public class CancelOrderCommandHandler extends ObservableCommandPublisher
    implements VoidCommandHandler<CancelOrderCommand> {

  public CancelOrderCommandHandler() {
    register(CancelOrderCommand.class, this);
  }

  @Override
  public void handle(CancelOrderCommand command) {

  }
}
