package com.br.bookdata.domain.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisCacheImplTest {

  @InjectMocks private RedisCacheImpl redisCache;

  @Mock private RedisTemplate<String, Object> redisTemplate;
  @Mock private ValueOperations<String, Object> valueOperations;

  @Mock private ObjectMapper objectMapper;

  private String cacheKey;
  private String cacheName;

  @BeforeEach
  void setUp() {
    cacheKey = "book-cache-key";
    cacheName = "book-cache";
  }

  @Test
  @DisplayName("Should retrieve data from cache on cache hit")
  void shouldGetFromCacheOnHit() {
    String prefixedKey = cacheName + ":" + cacheKey;
    Object value = new Object();
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(prefixedKey)).thenReturn(value);
    when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(value);

    Optional<Object> result =
        redisCache.getFromCache(cacheKey, new TypeReference<Object>() {}, cacheName);

    assertTrue(result.isPresent());
    assertEquals(value, result.get());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(prefixedKey);
  }

  @Test
  @DisplayName("Should return empty on cache miss")
  void shouldReturnEmptyOnCacheMiss() {
    String prefixedKey = cacheName + ":" + cacheKey;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(prefixedKey)).thenReturn(null);

    Optional<Object> result =
        redisCache.getFromCache(cacheKey, new TypeReference<Object>() {}, cacheName);

    assertFalse(result.isPresent());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(prefixedKey);
  }

  @Test
  @DisplayName("Should return empty on Redis connection failure")
  void shouldReturnEmptyOnRedisConnectionFailure() {
    String prefixedKey = cacheName + ":" + cacheKey;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(prefixedKey)).thenThrow(RedisConnectionFailureException.class);

    Optional<Object> result =
        redisCache.getFromCache(cacheKey, new TypeReference<Object>() {}, cacheName);

    assertFalse(result.isPresent());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(prefixedKey);
  }

  @Test
  @DisplayName("Should return empty on generic exception")
  void shouldReturnEmptyOnGenericExceptionFailure() {
    String prefixedKey = cacheName + ":" + cacheKey;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(prefixedKey)).thenThrow(RuntimeException.class);

    Optional<Object> result =
        redisCache.getFromCache(cacheKey, new TypeReference<Object>() {}, cacheName);

    assertFalse(result.isPresent());
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).get(prefixedKey);
  }

  @Test
  @DisplayName("Should store data in cache with expiration")
  void shouldPutToCacheWithExpiration() {
    Object value = new Object();
    Duration expiration = Duration.ofHours(1);
    String prefixedKey = cacheName + ":" + cacheKey;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    redisCache.putToCache(cacheKey, value, cacheName, expiration);

    verify(valueOperations, times(1)).set(prefixedKey, value, expiration);
  }

  @Test
  @DisplayName("Should store data in cache without expiration")
  void shouldPutToCacheWithoutExpiration() {
    Object value = new Object();
    String prefixedKey = cacheName + ":" + cacheKey;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    redisCache.putToCache(cacheKey, value, cacheName, null);

    verify(valueOperations, times(1)).set(prefixedKey, value);
  }

  @Test
  @DisplayName("Should not store data in cache on Redis connection failure")
  void shouldNotPutToCacheOnRedisConnectionFailure() {
    Object value = new Object();
    Duration expiration = Duration.ofHours(1);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    doThrow(RedisConnectionFailureException.class)
        .when(valueOperations)
        .set(anyString(), any(), any(Duration.class));

    redisCache.putToCache(cacheKey, value, cacheName, expiration);

    verify(valueOperations, times(1)).set(anyString(), any(), any(Duration.class));
  }

  @Test
  @DisplayName("Should not store data in cache on Illegal argument")
  void shouldNotPutToCacheOnIllegalArgumentExceptionFailure() {
    Object value = new Object();
    Duration expiration = Duration.ofHours(1);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    doThrow(IllegalArgumentException.class)
        .when(valueOperations)
        .set(anyString(), any(), any(Duration.class));

    redisCache.putToCache(cacheKey, value, cacheName, expiration);

    verify(valueOperations, times(1)).set(anyString(), any(), any(Duration.class));
  }

  @Test
  @DisplayName("Should not store data in cache on Exception")
  void shouldNotPutToCacheOnGenericExceptionFailure() {
    Object value = new Object();
    Duration expiration = Duration.ofHours(1);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    doThrow(RuntimeException.class)
        .when(valueOperations)
        .set(anyString(), any(), any(Duration.class));

    redisCache.putToCache(cacheKey, value, cacheName, expiration);

    verify(valueOperations, times(1)).set(anyString(), any(), any(Duration.class));
  }

  @Test
  @DisplayName("Should remove data from cache")
  void shouldRemoveFromCache() {
    redisCache.removeCache(cacheKey);

    verify(redisTemplate, times(1)).delete(cacheKey);
  }
}
