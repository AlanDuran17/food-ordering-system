package com.alanduran.order.service.domain.ports.output.repository;

import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);

    Optional<Order> findByOrderId(OrderId orderId);

}
