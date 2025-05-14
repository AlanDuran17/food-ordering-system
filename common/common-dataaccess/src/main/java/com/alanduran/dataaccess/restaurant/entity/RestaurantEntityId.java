package com.alanduran.dataaccess.restaurant.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntityId implements Serializable {

    private UUID restaurantId;
    private UUID productId;
}
