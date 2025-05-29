package com.alanduran.payment.service.messaging.mapper;

import com.alanduran.domain.valueobject.PaymentOrderStatus;
import com.alanduran.kafka.order.avro.model.PaymentRequestAvroModel;
import com.alanduran.kafka.order.avro.model.PaymentResponseAvroModel;
import com.alanduran.kafka.order.avro.model.PaymentStatus;
import com.alanduran.payment.service.domain.dto.PaymentRequest;
import com.alanduran.payment.service.domain.event.PaymentCancelledEvent;
import com.alanduran.payment.service.domain.event.PaymentCompletedEvent;
import com.alanduran.payment.service.domain.event.PaymentFailedEvent;
import com.alanduran.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId().toString())
                .sagaId(paymentRequestAvroModel.getSagaId().toString())
                .customerId(paymentRequestAvroModel.getCustomerId().toString())
                .orderId(paymentRequestAvroModel.getOrderId().toString())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(String sagaId,
                                                                                OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(sagaId))
                .setPaymentId(UUID.fromString(orderEventPayload.getPaymentId()))
                .setCustomerId(UUID.fromString(orderEventPayload.getCustomerId()))
                .setOrderId(UUID.fromString(orderEventPayload.getOrderId()))
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())//??
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}
