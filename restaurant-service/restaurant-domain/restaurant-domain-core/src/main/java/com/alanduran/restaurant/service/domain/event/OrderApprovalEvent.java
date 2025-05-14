package com.alanduran.restaurant.service.domain.event;

import com.alanduran.domain.event.DomainEvent;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.restaurant.service.domain.entity.OrderApproval;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class OrderApprovalEvent implements DomainEvent<OrderApproval> {

    private final OrderApproval orderApproval;
    private final RestaurantId restaurantId;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;
}
