package com.alanduran.order.service.domain.valueobject;

import com.alanduran.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long id) {
        super(id);
    }
}
