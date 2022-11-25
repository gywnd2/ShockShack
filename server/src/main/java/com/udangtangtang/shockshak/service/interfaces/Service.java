package com.udangtangtang.shockshak.service.interfaces;

public interface Service<T> {
    void register(T t);

    T remove(T t);

    T update(T t);
}
