package com.alanduran.domain.event;

public interface DomainEvent<T> {
    void fire();
}
