package com.inghubs.lock;

import java.util.UUID;

public interface LockPort {

  void lock(UUID customerId, String assetName);

  void unlock(UUID customerId, String assetName);

}
