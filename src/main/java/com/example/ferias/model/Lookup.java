package com.example.ferias.model;

public interface Lookup<T,U> {
    public T lookupBy(U key);
}
