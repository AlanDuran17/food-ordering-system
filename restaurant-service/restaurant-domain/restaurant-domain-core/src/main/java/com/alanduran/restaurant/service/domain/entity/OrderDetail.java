package com.alanduran.restaurant.service.domain.entity;

import com.alanduran.domain.entity.BaseEntity;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.domain.valueobject.OrderStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderDetail extends BaseEntity<OrderId> {

    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;

    private OrderDetail(Builder builder) {
        super.setId(builder.orderId);
        orderStatus = builder.orderStatus;
        totalAmount = builder.totalAmount;
        products = builder.products;
    }

    public static final class Builder {
        private OrderId orderId;
        private OrderStatus orderStatus;
        private Money totalAmount;
        private List<Product> products;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder totalAmount(Money val) {
            totalAmount = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public OrderDetail build() {
            return new OrderDetail(this);
        }
    }
}
