package com.alanduran.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.alanduran.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.alanduran.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantApprovalRequestMessagePublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessageOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
