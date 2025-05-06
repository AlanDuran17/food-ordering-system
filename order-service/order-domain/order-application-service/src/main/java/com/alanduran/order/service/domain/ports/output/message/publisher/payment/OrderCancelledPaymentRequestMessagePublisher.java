package com.alanduran.order.service.domain.ports.output.message.publisher.payment;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
