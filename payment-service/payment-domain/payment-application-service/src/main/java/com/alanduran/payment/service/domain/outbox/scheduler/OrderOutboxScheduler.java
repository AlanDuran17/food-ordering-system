package com.alanduran.payment.service.domain.outbox.scheduler;

import com.alanduran.outbox.OutboxScheduler;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.alanduran.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    public OrderOutboxScheduler(OrderOutboxHelper orderOutboxHelper, PaymentResponseMessagePublisher paymentResponseMessagePublisher) {
        this.orderOutboxHelper = orderOutboxHelper;
        this.paymentResponseMessagePublisher = paymentResponseMessagePublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${payment-service.outbox-scheduler-fixed-rate}",
             initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderOutboxMessage>> outboxMessagesResponse =
                orderOutboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if(outboxMessagesResponse.isPresent() && !outboxMessagesResponse.get().isEmpty()) {
            List<OrderOutboxMessage> outboxMessages = outboxMessagesResponse.get();
            log.info("Received {} OrderOutboxMessage with ids: {}, sending to kafka!", outboxMessages.size(),
                    outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(",")));
            outboxMessages.forEach(orderOutboxMessage ->
                    paymentResponseMessagePublisher.publish(orderOutboxMessage,
                            orderOutboxHelper::updateOutboxMessage));
            log.info("{} OrderOutboxMessage sent to message bus!", outboxMessages.size());
        }
    }
}
