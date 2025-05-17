package com.alanduran.order.service.dataaccess.order.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "order_items")
public class OrderItemEntityId implements Serializable {
    private Long id;
    private OrderEntity order;
}
