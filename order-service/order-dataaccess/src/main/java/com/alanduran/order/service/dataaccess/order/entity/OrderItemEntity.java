package com.alanduran.order.service.dataaccess.order.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "order"})
@IdClass(OrderItemEntityId.class)
@Table(name = "order_items")
@Entity
public class OrderItemEntity {


    @Id
    private Long id;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

    private UUID productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
}
