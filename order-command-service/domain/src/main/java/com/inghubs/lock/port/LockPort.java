package com.inghubs.lock.port;

public interface LockPort {

  void execute(Runnable task,
      String... lockKeys);

}
