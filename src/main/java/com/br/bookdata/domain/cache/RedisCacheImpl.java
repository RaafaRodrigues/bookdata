package com.br.bookdata.domain.cache;

import static java.util.Objects.isNull;

import com.br.bookdata.domain.cache.contract.ICache;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("redisCacheImpl")
@Log4j2
public final class RedisCacheImpl implements ICache {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  public RedisCacheImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public <T> Optional<T> getFromCache(
      String key, TypeReference<T> typeReference, String cacheName) {
    String prefixedKey = cacheName + ":" + key;
    try {
      log.debug("Attempting to retrieve data from cache. Key: {}, Cache Name: {}", key, cacheName);
      Object value = redisTemplate.opsForValue().get(prefixedKey);

      if (isNull(value)) {
        log.info("Cache miss. No value found for key: {}, Cache Name: {}", key, cacheName);
        return Optional.empty();
      }

      log.debug("Cache hit. Value found for key: {}, Cache Name: {}", key, cacheName);
      return Optional.of(objectMapper.convertValue(value, typeReference));
    } catch (RedisConnectionFailureException e) {
      log.warn(
          "Redis connection failure while retrieving value. Key: {}, Cache Name: {}. Error: {}",
          key,
          cacheName,
          e.getMessage(),
          e);
      return Optional.empty();
    } catch (Exception e) {
      log.error(
          "Unexpected error occurred while retrieving value from Redis. Key: {}, Cache Name: {}. Error: {}",
          key,
          cacheName,
          e.getMessage(),
          e);
      return Optional.empty();
    }
  }

  @Override
  public <T> void putToCache(String key, T value, String cacheName, Duration duration) {
    String prefixedKey = cacheName + ":" + key;
    try {
      log.debug(
          "Attempting to store data in cache. Key: {}, Cache Name: {}, Expiration: {}",
          key,
          cacheName,
          duration);
      Optional.ofNullable(duration)
          .ifPresentOrElse(
              expire -> redisTemplate.opsForValue().set(prefixedKey, value, expire),
              () -> redisTemplate.opsForValue().set(prefixedKey, value));

      log.info(
          "Successfully stored data in cache. Key: {}, Cache Name: {}, Expiration: {}",
          key,
          cacheName,
          duration);
    } catch (RedisConnectionFailureException e) {
      log.warn(
          "Redis connection failure while storing data. Key: {}, Cache Name: {}. Error: {}",
          key,
          cacheName,
          e.getMessage(),
          e);
    } catch (IllegalArgumentException e) {
      log.error(
          "Invalid argument provided for Redis cache. Key: {}, Cache Name: {}. Error: {}",
          key,
          cacheName,
          e.getMessage(),
          e);
    } catch (Exception e) {
      log.error(
          "Unexpected error occurred while storing data in Redis. Key: {}, Cache Name: {}. Error: {}",
          key,
          cacheName,
          e.getMessage(),
          e);
    }
  }

  @Override
  public void removeCache(String key) {
    log.debug("Attempting to delete data in cache. Key: {}", key);
    redisTemplate.delete(key);
    log.info("Successfully delete data in cache. Key: {}", key);
  }
}
