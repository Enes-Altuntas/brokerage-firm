package com.inghubs.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.data.redis")
@RequiredArgsConstructor
public class RedissonConfig {

  private String host;
  private Integer port;
  private String password;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(getRedisAddress()).setDatabase(15).setPassword(password);
    return Redisson.create(config);
  }

  private String getRedisAddress() {
    return String.format("redis://%s:%d", host, port);
  }
}

