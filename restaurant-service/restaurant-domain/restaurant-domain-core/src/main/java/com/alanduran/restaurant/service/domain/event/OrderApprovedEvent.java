package com.alanduran.restaurant.service.domain.event;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.restaurant.service.domain.entity.OrderApproval;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {
    public OrderApprovedEvent(OrderApproval orderApproval, RestaurantId restaurantId, List<String> failureMessages, ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
    }
}
