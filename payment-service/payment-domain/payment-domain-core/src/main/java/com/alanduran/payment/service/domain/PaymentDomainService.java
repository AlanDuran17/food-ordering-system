package com.alanduran.payment.service.domain;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.payment.service.domain.entity.CreditEntry;
import com.alanduran.payment.service.domain.entity.CreditHistory;
import com.alanduran.payment.service.domain.entity.Payment;
import com.alanduran.payment.service.domain.event.PaymentCancelledEvent;
import com.alanduran.payment.service.domain.event.PaymentCompletedEvent;
import com.alanduran.payment.service.domain.event.PaymentEvent;
import com.alanduran.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages, DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);

    PaymentEvent validateAndCancelPayment(Payment payment,
                                          CreditEntry creditEntry,
                                          List<CreditHistory> creditHistories,
                                          List<String> failureMessages, DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);
}
