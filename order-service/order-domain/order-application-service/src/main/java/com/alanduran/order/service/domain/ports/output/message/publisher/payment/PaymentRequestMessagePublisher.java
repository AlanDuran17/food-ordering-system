package com.alanduran.order.service.domain.ports.output.message.publisher.payment;

import com.alanduran.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.alanduran.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);
}
