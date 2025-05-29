package com.alanduran.order.service.domain;

import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.domain.valueobject.PaymentStatus;
import com.alanduran.order.service.domain.dto.message.PaymentResponse;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.event.OrderPaidEvent;
import com.alanduran.order.service.domain.exception.OrderDomainException;
import com.alanduran.order.service.domain.mapper.OrderDataMapper;
import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.alanduran.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.alanduran.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.alanduran.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.saga.SagaStatus;
import com.alanduran.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.alanduran.domain.DomainConstants.UTC_TIMEZONE;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService, OrderSagaHelper orderSagaHelper, PaymentOutboxHelper paymentOutboxHelper, ApprovalOutboxHelper approvalOutboxHelper, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(PaymentResponse data) {

        Optional<OrderPaymentOutboxMessage> paymentOutboxMessageResponse = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(data.getSagaId()),
                SagaStatus.STARTED
        );

        if (paymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", data.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = paymentOutboxMessageResponse.get();

        OrderPaidEvent domainEvent = completePaymentForOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus));

        approvalOutboxHelper.saveApprovalOutboxMessage(orderDataMapper.orderPaidEventToOrderApprovalEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(data.getSagaId()));

        log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getValue());
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse data) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(data.getSagaId()),
                        getCurrentSagaStatus(data.getPaymentStatus()));

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already rollbacked!", data.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        Order order = rollbackPaymentForOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage,
                order.getOrderStatus(), sagaStatus));

        if (data.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(data.getSagaId(),
                    order.getOrderStatus(), sagaStatus));
        }

        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.COMPENSATING);

        if (orderApprovalOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException("Approval outbox message could not be found in " + SagaStatus.COMPENSATED.name() + " status!");
        }
        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }

    private Order rollbackPaymentForOrder(PaymentResponse data) {
        log.info("Cancelling payment for order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.cancelOrder(order, data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[] { SagaStatus.STARTED };
            case CANCELLED -> new SagaStatus[] { SagaStatus.PROCESSING };
            case FAILED -> new SagaStatus[] { SagaStatus.STARTED, SagaStatus.PROCESSING };
        };
    }

}
