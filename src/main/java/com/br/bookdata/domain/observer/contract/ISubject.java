package com.br.bookdata.domain.observer.contract;

public interface ISubject<T> {
  void addObserver(IObserver<T> iObserver);

  void removeObserver(IObserver<T> iObserver);

  void notifyObservers(T eventData);
}
