package com.alanduran.payment.service.messaging.publisher.kafka;

import com.alanduran.kafka.order.avro.model.PaymentResponseAvroModel;
import com.alanduran.kafka.producer.KafkaMessageHelper;
import com.alanduran.kafka.producer.service.KafkaProducer;
import com.alanduran.payment.service.domain.config.PaymentServiceConfigData;
import com.alanduran.payment.service.domain.event.PaymentCompletedEvent;
import com.alanduran.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.alanduran.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompletedKafkaMessagePublisher implements PaymentCompletedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentCompletedKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper, KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer, PaymentServiceConfigData paymentServiceCOnfigData, KafkaMessageHelper kafkaMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceCOnfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();

        log.info("Received PaymentCompletedEvent for order id: {}", orderId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(domainEvent);

            kafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseAvroModel,
                    kafkaMessageHelper.getKafkaCallback(paymentServiceConfigData.getPaymentRequestTopicName(),
                            paymentResponseAvroModel,
                            orderId,
                            ""));

            log.info("PaymentRequestAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message" +
                    " to Kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
