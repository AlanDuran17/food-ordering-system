package order.service.dataaccess.restaurant.mapper;

import com.alanduran.domain.valueobject.Money;
import com.alanduran.domain.valueobject.ProductId;
import com.alanduran.domain.valueobject.RestaurantId;
import com.alanduran.order.service.domain.entity.Product;
import com.alanduran.order.service.domain.entity.Restaurant;
import order.service.dataaccess.restaurant.entity.RestaurantEntity;
import order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity =
                restaurantEntities.stream().findFirst().orElseThrow(
                        () -> new RestaurantDataAccessException("Restaurant could not be found"));

        List<Product> restaurantProducts = restaurantEntities.stream().map(entity ->
                new Product(new ProductId(entity.getProductID()), entity.getProductName(),
                        new Money(entity.getProductPrice()))).toList();

        return Restaurant.Builder.newBuilder()
                .id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(restaurantProducts)
                .active(restaurantEntity.getRestaurantActive())
                .build();

    }
}
