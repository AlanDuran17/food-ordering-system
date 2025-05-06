package com.alanduran.order.service.domain.entity;

import com.alanduran.domain.entity.BaseEntity;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.domain.valueobject.ProductId;
import lombok.Getter;

@Getter
public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId id, String name, Money price) {
        super.setId(id);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId id) {
        super.setId(id);
    }


    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
