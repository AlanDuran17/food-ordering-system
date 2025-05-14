package com.alanduran.restaurant.service.domain.mapper;

import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.alanduran.restaurant.service.domain.entity.OrderDetail;
import com.alanduran.restaurant.service.domain.entity.Product;
import com.alanduran.restaurant.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalRequestAvroModelToRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(OrderDetail.Builder.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                        .products(restaurantApprovalRequest.getProducts().stream().map(
                                product -> Product.Builder.builder()
                                        .productId(product.getId())
                                        .quantity(product.getQuantity())
                                        .build()
                        ).collect(Collectors.toList()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                        .build()
                )
                .build();
    }
}
