package com.alanduran.payment.service.domain.ports.output.message.publisher;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.payment.service.domain.event.PaymentFailedEvent;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
