package com.alanduran.order.service.domain;

import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.event.OrderCancelledEvent;
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
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderApprovalSaga(OrderDomainService orderDomainService, OrderSagaHelper orderSagaHelper, PaymentOutboxHelper paymentOutboxHelper, ApprovalOutboxHelper approvalOutboxHelper, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse data) {

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageOptional = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(data.getSagaId()),
                SagaStatus.PROCESSING);

        if (orderApprovalOutboxMessageOptional.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", data.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageOptional.get();

        Order order = approveOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(data.getSagaId(), order.getOrderStatus(), sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse data) {

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageOptional = approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(data.getSagaId()),
                SagaStatus.PROCESSING);

        if (orderApprovalOutboxMessageOptional.isEmpty()) {
            log.info("An outbox message with saga id: {} is already roll backed!", data.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageOptional.get();

        OrderCancelledEvent domainEvent = rollbackOrder(data);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage,
                domainEvent.getOrder().getOrderStatus(), sagaStatus));

        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(data.getSagaId()));

        log.info("Order with id: {} is cancelled", domainEvent.getOrder().getId().getValue());
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private Order approveOrder(RestaurantApprovalResponse data) {
        log.info("Approving order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }


    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageOptional = paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                UUID.fromString(sagaId),
                SagaStatus.PROCESSING);
        if (orderPaymentOutboxMessageOptional.isEmpty()) {
            throw new OrderDomainException("Payment outbox message cannot be found in " + SagaStatus.PROCESSING.name() + " state!");
        }
        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageOptional.get();
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse data) {
        log.info("Cancelling payment for order with id: {}", data.getOrderId());
        Order order = orderSagaHelper.findOrder(data.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order,
                data.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return orderCancelledEvent;
    }
}
