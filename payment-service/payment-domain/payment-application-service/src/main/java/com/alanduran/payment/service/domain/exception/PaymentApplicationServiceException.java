package com.alanduran.payment.service.domain.exception;

import com.alanduran.domain.exception.DomainException;

public class PaymentApplicationServiceException extends DomainException {
    public PaymentApplicationServiceException(String message) {
        super(message);
    }

    public PaymentApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
