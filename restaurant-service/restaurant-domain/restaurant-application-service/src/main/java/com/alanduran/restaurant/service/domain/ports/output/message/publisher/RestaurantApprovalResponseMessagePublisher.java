package com.alanduran.restaurant.service.domain.ports.output.message.publisher;

import com.alanduran.outbox.OutboxStatus;
import com.alanduran.restaurant.service.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantApprovalResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
