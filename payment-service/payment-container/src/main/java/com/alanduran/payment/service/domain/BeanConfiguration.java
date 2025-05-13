package com.alanduran.payment.service.domain;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
}
