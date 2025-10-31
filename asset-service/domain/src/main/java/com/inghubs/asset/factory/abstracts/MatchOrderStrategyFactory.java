package com.inghubs.asset.factory.abstracts;

import com.inghubs.asset.command.MatchOrderCommand;

public interface MatchOrderStrategyFactory {

  void matchOrder(MatchOrderCommand command);

}
