package com.alanduran.payment.service.domain.ports.output.repository;

import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
