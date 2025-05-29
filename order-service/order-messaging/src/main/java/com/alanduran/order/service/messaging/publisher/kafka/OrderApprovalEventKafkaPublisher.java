package com.alanduran.order.service.messaging.publisher.kafka;

import com.alanduran.kafka.order.avro.model.PaymentRequestAvroModel;
import com.alanduran.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.alanduran.kafka.producer.KafkaMessageHelper;
import com.alanduran.kafka.producer.service.KafkaProducer;
import com.alanduran.order.service.domain.config.OrderServiceConfigData;
import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.alanduran.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.alanduran.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.alanduran.outbox.OutboxStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderApprovalEventKafkaPublisher(OrderMessagingDataMapper orderMessagingDataMapper, KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer, OrderServiceConfigData orderServiceConfigData, KafkaMessageHelper kafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage, BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {
        OrderApprovalEventPayload orderApprovalEventPayload =
                kafkaMessageHelper.getOrderPaymentEventPayload(orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);

        String sagaId = orderApprovalOutboxMessage.getSagaId().toString();

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel = orderMessagingDataMapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId, orderApprovalEventPayload);

            kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    sagaId,
                    restaurantApprovalRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            orderApprovalOutboxMessage,
                            outboxCallback,
                            orderApprovalEventPayload.getOrderId(),
                            "RestaurantApprovalRequestAvroModel"));

            log.info("OrderApprovalEventPayload sent to Kafka for order id: {} and saga id: {}",
                    orderApprovalEventPayload.getOrderId(), sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderApprovalEventPayload to Kafka for order id: {} and saga id: {}",
                    orderApprovalEventPayload.getOrderId(), sagaId, e);
        }
    }
}
