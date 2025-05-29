package com.alanduran.payment.service.domain.outbox.model;

import com.alanduran.domain.valueobject.PaymentStatus;
import com.alanduran.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage {
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    @Setter
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Setter
    private PaymentStatus paymentStatus;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;
}
