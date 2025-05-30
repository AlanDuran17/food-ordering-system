package com.alanduran.restaurant.service.domain;

import com.alanduran.domain.event.publisher.DomainEventPublisher;
import com.alanduran.domain.valueobject.OrderApprovalStatus;
import com.alanduran.restaurant.service.domain.entity.Restaurant;
import com.alanduran.restaurant.service.domain.event.OrderApprovalEvent;
import com.alanduran.restaurant.service.domain.event.OrderApprovedEvent;
import com.alanduran.restaurant.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.alanduran.domain.DomainConstants.UTC_TIMEZONE;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService{


    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages) {

        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if(failureMessages.isEmpty()) {
            log.info("Order is approved for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        } else {
            log.info("Order is rejected for order id: {}", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC_TIMEZONE)));
        }
    }
}
