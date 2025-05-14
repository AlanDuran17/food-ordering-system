package com.alanduran.restaurant.service.domain.ports.output.repository;

import com.alanduran.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
