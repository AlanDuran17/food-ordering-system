package com.alanduran.order.service.domain.ports.output.message.publisher.payment;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
