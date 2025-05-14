package com.alanduran.restaurant.service.domain.ports.input.message.listener;

import com.alanduran.restaurant.service.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {

    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
