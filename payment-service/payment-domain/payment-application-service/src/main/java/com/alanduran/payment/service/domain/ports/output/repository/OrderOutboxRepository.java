package com.alanduran.payment.service.domain.ports.output.repository;

import com.alanduran.domain.valueobject.PaymentStatus;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {

    OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

    Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus status);

    Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                                    UUID sagaId,
                                                                                    PaymentStatus paymentStatus,
                                                                                    OutboxStatus outboxStatus);

    void deleteByTypeAndOutboxStatus(String type, OutboxStatus status);
}
