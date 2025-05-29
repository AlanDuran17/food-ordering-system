package com.alanduran.order.service.domain;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.entity.Restaurant;
import com.alanduran.order.service.domain.event.OrderCancelledEvent;
import com.alanduran.order.service.domain.event.OrderCreatedEvent;
import com.alanduran.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
