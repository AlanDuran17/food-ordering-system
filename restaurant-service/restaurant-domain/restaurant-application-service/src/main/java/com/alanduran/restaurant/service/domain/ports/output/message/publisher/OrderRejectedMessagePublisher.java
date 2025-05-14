package com.alanduran.restaurant.service.domain.ports.output.message.publisher;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
