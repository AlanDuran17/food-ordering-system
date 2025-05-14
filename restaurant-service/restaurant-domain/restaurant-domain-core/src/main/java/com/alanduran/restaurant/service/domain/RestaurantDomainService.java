package com.alanduran.restaurant.service.domain;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.restaurant.service.domain.entity.Restaurant;
import com.alanduran.restaurant.service.domain.event.OrderApprovalEvent;
import com.alanduran.restaurant.service.domain.event.OrderApprovedEvent;
import com.alanduran.restaurant.service.domain.event.OrderRejectedEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages,
                                     DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
                                     DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher);
}
