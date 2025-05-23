package com.alanduran.order.service.domain.mapper;

import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.domain.valueobject.ProductId;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.order.service.domain.dto.track.TrackOrderResponse;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.entity.OrderItem;
import com.alanduran.order.service.domain.entity.Product;
import com.alanduran.order.service.domain.entity.Restaurant;
import com.alanduran.order.service.domain.valueobject.StreetAddress;
import com.alanduran.order.service.domain.dto.create.CreateOrderCommand;
import com.alanduran.order.service.domain.dto.create.CreateOrderResponse;
import com.alanduran.order.service.domain.dto.create.OrderAddress;
import javax.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.Builder.newBuilder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                        new Product(
                                new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList()))
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.Builder.newBuilder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .orderItemList(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(@NotNull List<com.alanduran.order.service.domain.dto.create.OrderItem> items) {
        return items.stream().map(orderItem ->
                OrderItem.Builder.newBuilder()
                        .product(new Product(new ProductId(orderItem.getProductId())))
                        .price(new Money(orderItem.getPrice()))
                        .quantity(orderItem.getQuantity())
                        .subtotal(new Money(orderItem.getSubTotal()))
                        .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(@NotNull OrderAddress address) {
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }
}
