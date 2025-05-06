package com.alanduran.domain.event.publisher;

import com.alanduran.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
