package com.br.bookdata.domain.observer.contract;

public interface IObserver<T> {
  void update(T object);
}
