package com.alanduran.order.service.domain.event;

import com.alanduran.domain.event.DomainEvent;
import com.alanduran.order.service.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public abstract class OrderEvent implements DomainEvent<Order> {

    private final Order order;
    private final ZonedDateTime createdAt;
}
