package com.alanduran.saga;

import com.alanduran.domain.event.DomainEvent;

public interface SagaStep<T> {
    void process(T data);
    void rollback(T data);
}
