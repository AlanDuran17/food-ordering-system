package com.alanduran.payment.service.domain.ports.output.message.publisher;

import com.alanduran.outbox.OutboxStatus;
import com.alanduran.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
