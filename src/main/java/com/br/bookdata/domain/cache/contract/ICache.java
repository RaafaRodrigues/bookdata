package com.br.bookdata.domain.cache.contract;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.Optional;

public interface ICache {
  <T> Optional<T> getFromCache(String key, TypeReference<T> typeReference, String cacheName);

  <T> void putToCache(String key, T value, String cacheName, Duration duration);

  void removeCache(String key);
}
