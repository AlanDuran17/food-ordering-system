package com.alanduran.order.service.dataaccess.restaurant.adapter;

import com.alanduran.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.alanduran.order.service.domain.entity.Restaurant;
import com.alanduran.order.service.domain.ports.output.repository.RestaurantRepository;
import com.alanduran.dataaccess.restaurant.entity.RestaurantEntity;
import com.alanduran.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository repository;
    private final RestaurantDataAccessMapper mapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository repository, RestaurantDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts = mapper.restaurantToRestaurantProducts(restaurant);

        Optional<List<RestaurantEntity>> restaurantEntities = repository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts);

        return restaurantEntities.map(mapper::restaurantEntityToRestaurant);
    }
}
