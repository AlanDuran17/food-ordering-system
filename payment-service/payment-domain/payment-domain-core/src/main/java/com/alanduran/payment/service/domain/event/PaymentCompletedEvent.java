package com.alanduran.payment.service.domain.event;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class PaymentCompletedEvent extends PaymentEvent{

    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentCompletedEvent(Payment payment, ZonedDateTime createdAt, DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
