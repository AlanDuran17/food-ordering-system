package com.alanduran.order.service.domain;

import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.exception.OrderNotFoundException;
import com.alanduran.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderSagaHelper {

    private final OrderRepository orderRepository;

    public OrderSagaHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    Order findOrder(String orderId) {
        Optional<Order> orderResponse = orderRepository.findByOrderId(new OrderId(UUID.fromString(orderId)));
        if (orderResponse.isEmpty()) {
            log.error("Order with id: {} was not found", orderId);
            throw new OrderNotFoundException("Order with id: " + orderId + " was not found");
        }
        return orderResponse.get();
    }

    void saveOrder(Order order) {
        orderRepository.save(order);
    }
}
