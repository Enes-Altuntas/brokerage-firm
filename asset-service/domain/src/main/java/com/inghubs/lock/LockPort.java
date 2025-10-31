package com.inghubs.lock;

public interface LockPort {

  void execute(Runnable task,
      String... lockKeys);
}
