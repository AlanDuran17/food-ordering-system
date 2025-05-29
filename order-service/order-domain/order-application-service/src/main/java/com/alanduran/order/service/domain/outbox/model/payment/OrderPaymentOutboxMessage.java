package com.alanduran.order.service.domain.outbox.model.payment;

import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderPaymentOutboxMessage {

    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    @Setter
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Setter
    private SagaStatus sagaStatus;
    @Setter
    private OrderStatus orderStatus;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;
}
