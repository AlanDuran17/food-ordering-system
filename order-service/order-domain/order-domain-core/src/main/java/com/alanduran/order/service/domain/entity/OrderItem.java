package com.alanduran.order.service.domain.entity;

import com.alanduran.domain.entity.BaseEntity;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.domain.valueobject.OrderId;
import com.alanduran.order.service.domain.valueobject.OrderItemId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private final int quantity;
    private final Money price;
    private final Money subtotal;

    private OrderItem(Builder builder) {
        super.setId(builder.orderItemId);
        setOrderId(builder.orderId);
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subtotal = builder.subtotal;
    }

    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    boolean isPriceValid() {
        return price.isGreaterThanZero() &&
                price.equals(product.getPrice()) &&
                price.multiply(quantity).equals(subtotal);
    }


    public static final class Builder {
        private OrderItemId orderItemId;
        private OrderId orderId;
        private Product product;
        private int quantity;
        private Money price;
        private Money subtotal;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder subtotal(Money val) {
            subtotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
