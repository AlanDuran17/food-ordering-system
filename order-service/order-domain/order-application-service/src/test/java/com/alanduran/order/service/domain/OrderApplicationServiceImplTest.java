package com.alanduran.order.service.domain;

import com.alanduran.domain.valueobject.*;
import com.alanduran.order.service.domain.dto.create.CreateOrderCommand;
import com.alanduran.order.service.domain.dto.create.CreateOrderResponse;
import com.alanduran.order.service.domain.dto.create.OrderAddress;
import com.alanduran.order.service.domain.dto.create.OrderItem;
import com.alanduran.order.service.domain.entity.Customer;
import com.alanduran.order.service.domain.entity.Order;
import com.alanduran.order.service.domain.entity.Product;
import com.alanduran.order.service.domain.entity.Restaurant;
import com.alanduran.order.service.domain.exception.OrderDomainException;
import com.alanduran.order.service.domain.mapper.OrderDataMapper;
import com.alanduran.order.service.domain.ports.input.service.OrderApplicationService;
import com.alanduran.order.service.domain.ports.output.repository.CustomerRepository;
import com.alanduran.order.service.domain.ports.output.repository.OrderRepository;
import com.alanduran.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
class OrderApplicationServiceImplTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommandSuccess;
    private CreateOrderCommand createOrderCommandFailurePrice;
    private CreateOrderCommand createOrderCommandFailureProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("4e18c364-72b9-46dd-91a3-bb712e6d9069");
    private final UUID RESTAURANT_ID = UUID.fromString("4e18c364-72b9-46dd-91a3-bb712e6d9069");
    private final UUID PRODUCT_ID = UUID.fromString("4e18c364-72b9-46dd-91a3-bb712e6d9069");
    private final UUID ORDER_ID = UUID.fromString("4e18c364-72b9-46dd-91a3-bb712e6d9069");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    void setUp() {
        createOrderCommandSuccess = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Av. Corrientes")
                        .postalCode("1409")
                        .city("CABA")
                        .build())
                .price(PRICE)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandFailurePrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Av. Corrientes")
                        .postalCode("1409")
                        .city("CABA")
                        .build())
                .price(new BigDecimal("5000.00"))
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandFailureProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Av. Corrientes")
                        .postalCode("1409")
                        .city("CABA")
                        .build())
                .price(BigDecimal.valueOf(1000))
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("70.00"))
                                .subTotal(new BigDecimal("70.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.Builder.newBuilder()
                .id(new RestaurantId(createOrderCommandSuccess.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommandSuccess);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandSuccess)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommandSuccess);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order Created Successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandFailurePrice));
        assertEquals("Total price of: $5000.00 is not equal to total items price of: $200.00", orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandFailureProductPrice));
        assertEquals("Order item price: $70.00 is not valid for product " + PRODUCT_ID, orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        Restaurant restaurantResponse = Restaurant.Builder.newBuilder()
                .id(new RestaurantId(createOrderCommandSuccess.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(false)
                .build();
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandSuccess)))
                .thenReturn(Optional.of(restaurantResponse));

        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandSuccess));

        assertEquals("Restaurant with id " + RESTAURANT_ID + " is currently not active", orderDomainException.getMessage());
    }

    @Test
    void trackOrder() {
    }
}