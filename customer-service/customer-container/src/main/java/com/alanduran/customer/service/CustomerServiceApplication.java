package com.alanduran.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = { "com.alanduran.customer.service.dataaccess", "com.alanduran.dataaccess"})
@EntityScan(basePackages = { "com.alanduran.customer.service.dataaccess", "com.alanduran.dataaccess" })
@SpringBootApplication(scanBasePackages = "com.alanduran")
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
