package com.alanduran.payment.service.messaging.mapper;

import com.alanduran.domain.valueobject.PaymentOrderStatus;
import com.alanduran.kafka.order.avro.model.PaymentRequestAvroModel;
import com.alanduran.kafka.order.avro.model.PaymentResponseAvroModel;
import com.alanduran.kafka.order.avro.model.PaymentStatus;
import com.alanduran.payment.service.domain.dto.PaymentRequest;
import com.alanduran.payment.service.domain.event.PaymentCancelledEvent;
import com.alanduran.payment.service.domain.event.PaymentCompletedEvent;
import com.alanduran.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentCompletedEvent.getPayment().getId().getValue())
                .setCustomerId(paymentCompletedEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentCompletedEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentCompletedEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCompletedEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCompletedEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentCancelledEvent.getPayment().getId().getValue())
                .setCustomerId(paymentCancelledEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentCancelledEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentCancelledEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentCancelledEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentCancelledEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEventEvent) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.randomUUID())
                .setPaymentId(paymentFailedEventEvent.getPayment().getId().getValue())
                .setCustomerId(paymentFailedEventEvent.getPayment().getCustomerId().getValue())
                .setOrderId(paymentFailedEventEvent.getPayment().getOrderId().getValue())
                .setPrice(paymentFailedEventEvent.getPayment().getPrice().getAmount())
                .setCreatedAt(paymentFailedEventEvent.getPayment().getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(paymentFailedEventEvent.getPayment().getPaymentStatus().name()))
                .setFailureMessages(paymentFailedEventEvent.getFailureMessages())
                .build();
    }

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
}
