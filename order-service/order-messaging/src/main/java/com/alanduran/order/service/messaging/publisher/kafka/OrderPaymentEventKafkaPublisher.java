package com.alanduran.order.service.messaging.publisher.kafka;

import com.alanduran.kafka.order.avro.model.PaymentRequestAvroModel;
import com.alanduran.kafka.producer.KafkaMessageHelper;
import com.alanduran.kafka.producer.service.KafkaProducer;
import com.alanduran.order.service.domain.config.OrderServiceConfigData;
import com.alanduran.order.service.domain.exception.OrderDomainException;
import com.alanduran.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.alanduran.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.alanduran.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.alanduran.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.alanduran.outbox.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderPaymentEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper, KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer, OrderServiceConfigData orderServiceConfigData, KafkaMessageHelper kafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage, BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {
        OrderPaymentEventPayload orderPaymentEventPayload = kafkaMessageHelper.getOrderPaymentEventPayload(
                orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);

        String sagaId = orderPaymentOutboxMessage.getSagaId().toString();

        log.info("Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
                orderPaymentEventPayload.getOrderId(),
                sagaId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(sagaId, orderPaymentEventPayload);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    sagaId,
                    paymentRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentRequestTopicName(),
                            paymentRequestAvroModel,
                            orderPaymentOutboxMessage,
                            outboxCallback,
                            orderPaymentEventPayload.getOrderId(),
                            "PaymentRequestAvroModel"));

            log.info("OrderPaymentEventPayload sent to Kafka for order id: {} and saga id: {}",
                    orderPaymentEventPayload.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderPaymentEventPayload to Kafka for order id: {} and saga id: {}",
                    orderPaymentEventPayload.getOrderId(), sagaId, e);
        }

    }
}
