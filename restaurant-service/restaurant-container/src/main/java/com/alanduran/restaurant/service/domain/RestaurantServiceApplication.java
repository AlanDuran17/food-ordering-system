package com.alanduran.restaurant.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.alanduran.restaurant.service.dataaccess")
@EntityScan(basePackages = "com.alanduran.restaurant.service.dataaccess")
@SpringBootApplication(scanBasePackages = "com.alanduran")
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class);
    }
}
