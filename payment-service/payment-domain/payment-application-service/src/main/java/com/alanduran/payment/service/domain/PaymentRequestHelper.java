package com.alanduran.payment.service.domain;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.payment.service.domain.dto.PaymentRequest;
import com.alanduran.payment.service.domain.event.PaymentCancelledEvent;
import com.alanduran.payment.service.domain.event.PaymentCompletedEvent;
import com.alanduran.payment.service.domain.event.PaymentFailedEvent;
import com.alanduran.payment.service.domain.exception.PaymentApplicationServiceException;
import com.alanduran.payment.service.domain.mapper.PaymentDataMapper;
import com.alanduran.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.alanduran.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.alanduran.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.alanduran.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.alanduran.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.alanduran.payment.service.domain.ports.output.repository.PaymentRepository;
import com.alanduran.payment.service.domain.entity.CreditEntry;
import com.alanduran.payment.service.domain.entity.CreditHistory;
import com.alanduran.payment.service.domain.entity.Payment;
import com.alanduran.payment.service.domain.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;
    private final PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
    private final PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;

    public PaymentRequestHelper(PaymentDomainService paymentDomainService, PaymentDataMapper paymentDataMapper, PaymentRepository paymentRepository, CreditEntryRepository creditEntryRepository, CreditHistoryRepository creditHistoryRepository, PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher, PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher, PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
        this.paymentFailedEventDomainEventPublisher = paymentFailedEventDomainEventPublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();

        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories, failureMessages, paymentCompletedEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        persistDbPayment(payment, failureMessages, creditEntry, creditHistories);

        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelEvent(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

        if (paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment with order id: " + paymentRequest.getOrderId() + " could not be found");
        }

        Payment payment = paymentResponse.get();
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistories(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories, failureMessages, paymentCancelledEventDomainEventPublisher, paymentFailedEventDomainEventPublisher);

        persistDbPayment(payment, failureMessages, creditEntry, creditHistories);
        return paymentEvent;
    }

    private void persistDbPayment(Payment payment, List<String> failureMessages, CreditEntry creditEntry, List<CreditHistory> creditHistories) {
        paymentRepository.save(payment);

        if (failureMessages.isEmpty()){
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get((creditHistories.size() - 1)));
        }
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {

        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);

        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: " + customerId.getValue());
        }

        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistories(CustomerId customerId) {

        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.error("Could not find credit histories for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit histories for customer: " + customerId.getValue());
        }

        return creditHistories.get();
    }
}
