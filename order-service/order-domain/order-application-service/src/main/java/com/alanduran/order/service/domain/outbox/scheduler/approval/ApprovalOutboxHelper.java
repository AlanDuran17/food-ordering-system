package com.alanduran.order.service.domain.outbox.scheduler.approval;

import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.order.service.domain.exception.OrderDomainException;
import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.alanduran.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.alanduran.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.saga.SagaStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.alanduran.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;
    private final ObjectMapper objectMapper;

    public ApprovalOutboxHelper(ApprovalOutboxRepository approvalOutboxRepository, ObjectMapper objectMapper) {
        this.approvalOutboxRepository = approvalOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus, SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME,
                outboxStatus,
                sagaStatuses);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatuses) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(ORDER_SAGA_NAME, sagaId, sagaStatuses);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage response = approvalOutboxRepository.save(orderApprovalOutboxMessage);
        if (response == null) {
            log.error("Could not save OrderApprovalOutboxMessage with outbox id: {}", orderApprovalOutboxMessage.getId());
            throw new OrderDomainException("Could not save OrderApprovalOutboxMessage with outbox id: " + orderApprovalOutboxMessage.getId());
        }
        log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                       SagaStatus... sagaStatuses) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, outboxStatus, sagaStatuses);
    }

    @Transactional
    public void saveApprovalOutboxMessage(OrderApprovalEventPayload orderApprovalEventPayload,
                                          OrderStatus orderStatus,
                                          SagaStatus sagaStatus,
                                          OutboxStatus outboxStatus,
                                          UUID sagaId) {

        save(OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(orderApprovalEventPayload.getCreatedAt())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderApprovalEventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build());
    }

    private String createPayload(OrderApprovalEventPayload orderApprovalEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderApprovalEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderApprovalOutboxMessage for order id: {}", orderApprovalEventPayload.getOrderId());
            throw new OrderDomainException("Could not create OrderApprovalOutboxMessage for order id: " + orderApprovalEventPayload.getOrderId());
        }
    }
}
