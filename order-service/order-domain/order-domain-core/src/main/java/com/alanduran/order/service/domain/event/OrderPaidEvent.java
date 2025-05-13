package com.alanduran.order.service.domain.event;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderPaidEvent extends OrderEvent {

    private final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;

    public OrderPaidEvent(Order order, ZonedDateTime createdAt, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
        super(order, createdAt);
        this.orderPaidEventDomainEventPublisher = orderPaidEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        orderPaidEventDomainEventPublisher.publish(this);
    }
}