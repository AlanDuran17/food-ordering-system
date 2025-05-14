package com.alanduran.restaurant.service.domain.ports.output.message.publisher;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.restaurant.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
