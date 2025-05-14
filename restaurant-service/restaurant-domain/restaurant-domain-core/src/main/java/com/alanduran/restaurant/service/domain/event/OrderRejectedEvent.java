package com.alanduran.restaurant.service.domain.event;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher;

    public OrderRejectedEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt, DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        orderRejectedEventDomainEventPublisher.publish(this);
    }
}
