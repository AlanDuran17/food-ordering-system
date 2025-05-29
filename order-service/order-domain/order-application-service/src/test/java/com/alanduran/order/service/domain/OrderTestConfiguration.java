package com.alanduran.order.service.domain;

import com.alanduran.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.alanduran.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.alanduran.order.service.domain.ports.output.repository.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootApplication(scanBasePackages = "com.alanduran")
public class OrderTestConfiguration {

    @Bean
    public PaymentRequestMessagePublisher PaymentRequestMessagePublisher() {
        return mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher() {
        return mock(RestaurantApprovalRequestMessagePublisher.class);
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository() {
        return mock(ApprovalOutboxRepository.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return mock(PaymentOutboxRepository.class);
    }
    @Bean
    public OrderRepository orderRepository() {
        return mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return mock(CustomerRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return mock(RestaurantRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
