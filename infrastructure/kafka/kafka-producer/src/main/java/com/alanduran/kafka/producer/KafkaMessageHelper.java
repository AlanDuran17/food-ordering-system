package com.alanduran.kafka.producer;

import com.alanduran.order.service.domain.exception.OrderDomainException;
import com.alanduran.outbox.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public KafkaMessageHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T, U> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(String paymentResponseTopicName, T avroModel, U outboxMessage,
                                                                                   BiConsumer<U, OutboxStatus> outboxCallback, String orderId, String avroModelName) {
        return new ListenableFutureCallback<SendResult<String, T>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending {} with message: {} amd outbox type: {} to topic {}", avroModelName,
                        avroModel.toString(), outboxMessage.getClass().getName(), paymentResponseTopicName, ex);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata metadata = result.getRecordMetadata();

                log.info("Received Successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }

    public <T> T getOrderPaymentEventPayload(String payload, Class<T> outputType) {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", outputType.getName(),  e);
            throw new OrderDomainException("Could not read " + outputType.getName() + " object!");
        }
    }
}
