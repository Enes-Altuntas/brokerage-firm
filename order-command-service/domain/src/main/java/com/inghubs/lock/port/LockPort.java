package com.inghubs.lock.port;

import java.util.UUID;

public interface LockPort {

  void lock(UUID orderId);

  void unlock(UUID orderId);
}
