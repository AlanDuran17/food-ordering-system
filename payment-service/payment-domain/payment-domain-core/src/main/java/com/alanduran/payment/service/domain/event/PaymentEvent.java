package com.alanduran.payment.service.domain.event;

import com.alanduran.domain.event.DomainEvent;
import com.alanduran.payment.service.domain.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class PaymentEvent implements DomainEvent<Payment> {

    private final Payment payment;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessages;
}
