package com.alanduran.restaurant.service.domain;

import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.alanduran.restaurant.service.domain.entity.Restaurant;
import com.alanduran.restaurant.service.domain.event.OrderApprovalEvent;
import com.alanduran.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.alanduran.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.alanduran.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.alanduran.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.alanduran.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import com.alanduran.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.alanduran.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher;
    private final OrderOutboxHelper orderOutboxHelper;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository, RestaurantApprovalResponseMessagePublisher restaurantApprovalResponseMessagePublisher, OrderOutboxHelper orderOutboxHelper) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.restaurantApprovalResponseMessagePublisher = restaurantApprovalResponseMessagePublisher;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Transactional
    public void persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        if (publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
            log.info("An outbox message with saga id: {} already saved to database!",
                    restaurantApprovalRequest.getSagaId());
            return;
        }

        log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());

        List<String> failureMessages = new ArrayList<>();

        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);

        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
                restaurant,
                failureMessages);

        orderApprovalRepository.save(restaurant.getOrderApproval());

        orderOutboxHelper
                .saveOrderOutboxMessage(restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalRequest.getSagaId()));
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantDataMapper.restaurantApprovalRequestAvroModelToRestaurant(restaurantApprovalRequest);

        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);

        if(restaurantResult.isEmpty()) {
            log.error("Restaurant with id " + restaurant.getId().getValue() + " not found");
            throw new RestaurantNotFoundException("Restaurant with id " + restaurant.getId().getValue() + " not found");
        }

        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().setTotalAmount(restaurantEntity.getOrderDetail().getTotalAmount());
        restaurant.getOrderDetail().getProducts().forEach(product ->
                restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                    if (p.getId().equals(product.getId())) {
                        product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                    }
                }));
        restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));

        return restaurant;
    }

    private boolean publishIfOutboxMessageProcessed(RestaurantApprovalRequest restaurantApprovalRequest) {
        Optional<OrderOutboxMessage> orderOutboxMessage = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                UUID.fromString(restaurantApprovalRequest.getSagaId()), OutboxStatus.COMPLETED);

        if (orderOutboxMessage.isPresent()) {
            restaurantApprovalResponseMessagePublisher.publish(orderOutboxMessage.get(),
                    orderOutboxHelper::updateOutboxStatus);
            return true;
        }

        return false;
    }
}
