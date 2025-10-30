package com.inghubs.adapters.lock;

import com.inghubs.common.exception.RedisLockException;
import com.inghubs.lock.LockPort;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetLockRedisAdapter implements LockPort {

  private final RedissonClient redissonClient;

  @Override
  public void lock(UUID customerId, String assetName) {
    RLock lock = redissonClient.getLock("asset:" + assetName + ":" + customerId.toString());
    try {
      lock.tryLock(5, 10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RedisLockException("2002");
    }
  }

  @Override
  public void unlock(UUID customerId, String assetName) {
    RLock lock = redissonClient.getLock("asset:" + assetName + ":" + customerId.toString());
    if (lock.isHeldByCurrentThread()) {
      lock.unlock();
    }
  }
}
