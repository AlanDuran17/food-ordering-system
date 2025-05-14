package com.alanduran.restaurant.service.domain.valueobject;

import com.alanduran.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID>{
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
