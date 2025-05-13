package com.alanduran.payment.service.domain.dto;

import com.alanduran.domain.valueobject.PaymentOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter(onParam_ = "")
@Builder
@AllArgsConstructor
public class PaymentRequest {
    private String id;
    private String sagaId;
    private String orderId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;

    @Setter
    private PaymentOrderStatus paymentOrderStatus;
}
