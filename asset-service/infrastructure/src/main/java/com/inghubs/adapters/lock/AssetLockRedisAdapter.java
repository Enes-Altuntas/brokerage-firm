package com.inghubs.adapters.lock;

import com.inghubs.common.exception.RedisLockException;
import com.inghubs.lock.LockPort;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetLockRedisAdapter implements LockPort {

  public static final int RELEASE_SECOND = 10;
  public static final int ACQUIRE_SECOND = 5;
  public static final String ORDER_LOCK_PREFIX = "asset:";

  private final RedissonClient redissonClient;

  @Override
  public void execute(Runnable task,
      String... lockKeys) {

    List<RLock> locks = Arrays.stream(lockKeys)
        .map(key -> redissonClient.getLock(ORDER_LOCK_PREFIX + key))
        .toList();

    RLock lock = new RedissonMultiLock(locks.toArray(new RLock[0]));

    try {
      if (!lock.tryLock(ACQUIRE_SECOND, RELEASE_SECOND, TimeUnit.SECONDS)) {
        throw new RedisLockException("2002");
      }
      task.run();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Interrupted while trying to acquire lock", e);
      throw new RedisLockException("2002");
    } catch (Exception e) {
      throw e;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        try {
          lock.unlock();
        } catch (Exception e) {
          log.error("Failed to unlock lock", e);
        }
      }
    }
  }
}
