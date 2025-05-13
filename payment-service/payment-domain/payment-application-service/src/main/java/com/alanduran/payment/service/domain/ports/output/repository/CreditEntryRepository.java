package com.alanduran.payment.service.domain.ports.output.repository;

import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
