package com.alanduran.payment.service.messaging.listener.kafka;

import com.alanduran.domain.valueobject.PaymentOrderStatus;
import com.alanduran.kafka.consumer.KafkaConsumer;
import com.alanduran.kafka.order.avro.model.PaymentRequestAvroModel;
import com.alanduran.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.alanduran.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {
    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener, PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${payment-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment requests received with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(paymentRequestAvroModel -> {
            if (PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()) == PaymentOrderStatus.PENDING) {
                log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            } else if (PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()) == PaymentOrderStatus.CANCELLED) {
                log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            }
        });
    }
}
