package com.inghubs.order.command;

import com.inghubs.common.model.Command;
import com.inghubs.common.model.Event;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CancelOrderCommand extends Event implements Command {

}
