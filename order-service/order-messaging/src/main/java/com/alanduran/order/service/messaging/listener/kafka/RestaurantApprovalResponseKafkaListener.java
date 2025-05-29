package com.alanduran.order.service.messaging.listener.kafka;

import com.alanduran.kafka.consumer.KafkaConsumer;
import com.alanduran.kafka.order.avro.model.OrderApprovalStatus;
import com.alanduran.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.alanduran.order.service.domain.exception.OrderNotFoundException;
import com.alanduran.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.alanduran.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private static final String FAILURE_MESSAGE_DELIMITER = "," ;
    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener, OrderMessagingDataMapper orderMessagingDataMapper) {
        this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload  List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of restaurant a´´rpvañ responses received with keys:{}, partitions:ñ{ and offsets:{}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            try {
                if (OrderApprovalStatus.APPROVED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                    log.info("Processing approved order for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());

                    restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
                } else if (OrderApprovalStatus.REJECTED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                    log.info("Processing rejected order for order id: {}, with failure messages: {}",
                            restaurantApprovalResponseAvroModel.getOrderId(), String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponseAvroModel.getFailureMessages()));
                    restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(restaurantApprovalResponseAvroModel));
                }
            } catch (OptimisticLockingFailureException e) {
                //NO-op for optimistic lock. This means another thread finished the work, do not throw error to prevent reading the data from kafka again!
                log.error("Caught optimistic locking exception in RestaurantApprovalResponseKafkaListener for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
            } catch (OrderNotFoundException e) {
                //NO-op for OrderNotFoundException
                log.error("No order found for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
            }
        });
    }
}
