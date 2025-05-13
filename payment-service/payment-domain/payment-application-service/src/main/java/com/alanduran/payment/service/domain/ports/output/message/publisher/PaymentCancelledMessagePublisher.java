package com.alanduran.payment.service.domain.ports.output.message.publisher;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
