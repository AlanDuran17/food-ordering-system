package com.alanduran.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
